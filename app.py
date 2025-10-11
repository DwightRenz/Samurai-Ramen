import streamlit as st
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import joblib

# Page configuration
st.set_page_config(
    page_title="NBA Dream Team Simulator",
    page_icon="🏀",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Custom CSS for better styling
st.markdown("""
    <style>
    .main-header {
        font-size: 3rem !important;
        color: #1e3a8a;
        text-align: center;
        margin-bottom: 1rem;
        background: linear-gradient(135deg, #C8102E 0%, #FDB927 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        font-weight: 800;
    }
    .sub-header {
        font-size: 1.5rem !important;
        color: #374151;
        text-align: center;
        margin-bottom: 2rem;
        font-weight: 300;
    }
    .team-card {
        background: white;
        border-radius: 15px;
        padding: 1.5rem;
        margin: 1rem 0;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        border-left: 4px solid #1e3a8a;
    }
    .probability-card {
        background: linear-gradient(135deg, #C8102E 0%, #FDB927 100%);

        color: white;
        border-radius: 15px;
        padding: 2rem;
        text-align: center;
        margin: 1rem 0;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
    }
    .player-tag {
        background: #C8102E;
        color: white !important;
        padding: 0.5rem 1rem;
        border-radius: 25px;
        margin: 0.3rem;
        display: inline-block;
        font-size: 0.9rem;
        font-weight: 500;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        transition: all 0.3s ease;
    }
    .player-tag:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(0,0,0,0.2);
    }
    .stats-card {
        background: #f8fafc;
        border-radius: 10px;
        padding: 1rem;
        margin: 0.5rem 0;
        border-left: 4px solid #10b981;
    }
    .stProgress > div > div > div > div {
        background: linear-gradient(90deg, #10b981 0%, #3b82f6 100%);
    }
    .stButton button {
        background: linear-gradient(135deg, #C8102E 0%, #FDB927 100%);
        color: black;
        border: none;
        border-radius: 10px;
        padding: 0.75rem 2rem;
        font-weight: 800;
        transition: all 0.3s ease;
    }
    .stButton button:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
    }
    /* Fix for multiselect */
    .stMultiSelect [data-baseweb="tag"] {
        background-color: #C8102E !important;
        color: white !important;
    }
    .stMultiSelect [data-baseweb="tag"] span {
        color: white !important;
    }
    .team-complete {
        border-left: 4px solid #10b981;
    }
    .team-incomplete {
        border-left: 4px solid #C8102E;
    }
    .feature-metric {
        text-align: center;
        padding: 1rem;
    }
    .feature-metric .value {
        font-size: 1.5rem;
        font-weight: bold;
        color: #1e3a8a;
    }
    .feature-metric .label {
        font-size: 0.8rem;
        color: #6b7280;
        text-transform: uppercase;
    }
    .empty-state {
        text-align: center;
        padding: 3rem 2rem;
        background: rgb(14, 17, 23;
        border-radius: 15px;
        margin: 1rem 0;
    }
    .empty-state-icon {
        font-size: 4rem;
        margin-bottom: 1rem;
    }
    </style>
""", unsafe_allow_html=True)

@st.cache_resource
def load_model():
    data = joblib.load("random_forest_matchup_model.joblib")
    return data["model"], data["features"]

model, feature_names = load_model()

@st.cache_data
def load_data():
    df = pd.read_csv("NBA_Player_Stats.csv")
    df.columns = df.columns.str.strip()
    return df

def build_team_features(player_names, df, team_label):
    team_df = df[df["Name"].isin(player_names)]
    agg_features = {}
    if not team_df.empty:
        agg_features = {
            f"{team_label}_reb": team_df["REB"].sum(),
            f"{team_label}_ast": team_df["AST"].sum(),
            f"{team_label}_stl": team_df["STL"].sum(),
            f"{team_label}_blk": team_df["BLK"].sum(),
            f"{team_label}_to": team_df["TO"].sum(),
            f"{team_label}_fg%": team_df["FG%"].mean(),
            f"{team_label}_3p%": team_df["3P%"].mean(),
            f"{team_label}_ft%": team_df["FT%"].mean()
        }
    return pd.Series(agg_features)

def simulate_matchup(teamA, teamB, df, model, feature_names):
    teamA_features = build_team_features(teamA, df, "TeamA")
    teamB_features = build_team_features(teamB, df, "TeamB")
    all_features = {**teamA_features.to_dict(), **teamB_features.to_dict()}
    matchup_df = pd.DataFrame([all_features], columns=feature_names)
    matchup_df = matchup_df.fillna(0)
    
    # Original prediction
    prob_original = model.predict_proba(matchup_df)[0][1]
    
    # Swapped features
    swapped_features = matchup_df.copy()
    swapped_features.columns = swapped_features.columns.str.replace("TeamA_", "TMP_")
    swapped_features.columns = swapped_features.columns.str.replace("TeamB_", "TeamA_")
    swapped_features.columns = swapped_features.columns.str.replace("TMP_", "TeamB_")
    swapped_features = swapped_features[feature_names].fillna(0)
    prob_swapped = model.predict_proba(swapped_features)[0][1]
    
    # Symmetric averaging
    teamA_win_prob = (prob_original + (1 - prob_swapped)) / 2
    teamB_win_prob = 1 - teamA_win_prob

    return [teamB_win_prob, teamA_win_prob]

def get_player_stats(player_names, df):
    """Get key stats for selected players"""
    stat_columns = ["Name", "PTS", "AST", "STL", "BLK"]
    if "REB" in df.columns:
        stat_columns.append("REB")
    elif "TRB" in df.columns:
        stat_columns.append("TRB")
    elif "Rebounds" in df.columns:
        stat_columns.append("Rebounds")
    stats = df[df["Name"].isin(player_names)][stat_columns].set_index("Name")
    return stats

def get_team_summary_stats(team_players, df):
    """Calculate team summary statistics"""
    team_df = df[df["Name"].isin(team_players)]
    if team_df.empty:
        return {}
    
    return {
        "Total Points": team_df["PTS"].sum(),
        "Avg Points": team_df["PTS"].mean(),
        "Total Assists": team_df["AST"].sum(),
        "Total Rebounds": team_df["REB"].sum() if "REB" in team_df.columns else team_df.get("TRB", team_df.get("Rebounds", pd.Series([0]))).sum(),
        "Avg FG%": team_df["FG%"].mean(),
        "Avg 3P%": team_df["3P%"].mean()
    }

# --- Improved Streamlit UI ---
st.markdown('<h1 class="main-header">🏀 NBA Dream Team Matchup Simulator</h1>', unsafe_allow_html=True)
st.markdown('<p class="sub-header">Build your ultimate 5-player teams and simulate epic NBA matchups</p>', unsafe_allow_html=True)

# Load data
df = load_data()
players = sorted(df["Name"].unique())

# Sidebar with improved organization
with st.sidebar:
    st.header("🏀 Team Builder")
    
    # Team A Section
    st.subheader("👑 Team A")
    teamA = st.multiselect(
        "Select 5 players for Team A",
        players,
        key="teamA",
        help="Choose 5 players to form Team A",
        max_selections=5,
        placeholder="Start typing to search players..."
    )
    
    # Show Team A summary
    if teamA:
        teamA_stats = get_team_summary_stats(teamA, df)
        st.caption(f"📊 Team A Stats Preview:")
        col1, col2 = st.columns(2)
        with col1:
            st.metric("Total PTS", f"{teamA_stats['Total Points']:.1f}")
            st.metric("Total AST", f"{teamA_stats['Total Assists']:.1f}")
        with col2:
            st.metric("Total REB", f"{teamA_stats['Total Rebounds']:.1f}")
            st.metric("Avg FG%", f"{teamA_stats['Avg FG%']:.1f}%")
    
    st.divider()
    
    # Team B Section
    st.subheader("⚡ Team B")
    teamB = st.multiselect(
        "Select 5 players for Team B",
        players,
        key="teamB",
        help="Choose 5 players to form Team B",
        max_selections=5,
        placeholder="Start typing to search players..."
    )
    
    # Show Team B summary
    if teamB:
        teamB_stats = get_team_summary_stats(teamB, df)
        st.caption(f"📊 Team B Stats Preview:")
        col1, col2 = st.columns(2)
        with col1:
            st.metric("Total PTS", f"{teamB_stats['Total Points']:.1f}")
            st.metric("Total AST", f"{teamB_stats['Total Assists']:.1f}")
        with col2:
            st.metric("Total REB", f"{teamB_stats['Total Rebounds']:.1f}")
            st.metric("Avg FG%", f"{teamB_stats['Avg FG%']:.1f}%")

# Main content area
col1, col2 = st.columns([1, 1])

with col1:
    teamA_class = "team-complete" if len(teamA) == 5 else "team-incomplete"
    st.markdown(f'<div class="team-card {teamA_class}">', unsafe_allow_html=True)
    st.subheader("👑 Team A")
    
    if teamA:
        st.write("**Selected Players:**")
        for player in teamA:
            st.markdown(f'<span class="player-tag">{player}</span>', unsafe_allow_html=True)
        
        # Team A quick stats
        if len(teamA) > 0:
            teamA_stats = get_team_summary_stats(teamA, df)
            st.markdown("---")
            st.write("**Team Summary:**")
            cols = st.columns(3)
            cols[0].metric("Players", f"{len(teamA)}/5")
            cols[1].metric("Avg PTS", f"{teamA_stats['Avg Points']:.1f}")
            cols[2].metric("FG%", f"{teamA_stats['Avg FG%']:.1f}%")
    else:
        st.markdown('<div class="empty-state">', unsafe_allow_html=True)
        st.markdown('<div class="empty-state-icon">👑</div>', unsafe_allow_html=True)
        st.info("💡 Select players from the sidebar to build Team A")
        st.markdown("</div>", unsafe_allow_html=True)
    
    st.markdown('</div>', unsafe_allow_html=True)

with col2:
    teamB_class = "team-complete" if len(teamB) == 5 else "team-incomplete"
    st.markdown(f'<div class="team-card {teamB_class}">', unsafe_allow_html=True)
    st.subheader("⚡ Team B")
    
    if teamB:
        st.write("**Selected Players:**")
        for player in teamB:
            st.markdown(f'<span class="player-tag">{player}</span>', unsafe_allow_html=True)
        
        # Team B quick stats
        if len(teamB) > 0:
            teamB_stats = get_team_summary_stats(teamB, df)
            st.markdown("---")
            st.write("**Team Summary:**")
            cols = st.columns(3)
            cols[0].metric("Players", f"{len(teamB)}/5")
            cols[1].metric("Avg PTS", f"{teamB_stats['Avg Points']:.1f}")
            cols[2].metric("FG%", f"{teamB_stats['Avg FG%']:.1f}%")
    else:
        st.markdown('<div class="empty-state">', unsafe_allow_html=True)
        st.markdown('<div class="empty-state-icon">⚡</div>', unsafe_allow_html=True)
        st.info("💡 Select players from the sidebar to build Team B")
        st.markdown("</div>", unsafe_allow_html=True)
    
    st.markdown('</div>', unsafe_allow_html=True)

# Team status and simulation button
st.markdown("---")
status_col1, status_col2, status_col3 = st.columns([2, 1, 2])

with status_col1:
    if len(teamA) == 5:
        st.success("✅ Team A is ready for matchup!")
    else:
        st.warning(f"🟡 Team A needs {5 - len(teamA)} more player(s)")

with status_col2:
    st.markdown("<h3 style='text-align: center;'>VS</h3>", unsafe_allow_html=True)

with status_col3:
    if len(teamB) == 5:
        st.success("✅ Team B is ready for matchup!")
    else:
        st.warning(f"🟡 Team B needs {5 - len(teamB)} more player(s)")

# Simulation and Results
if len(teamA) == 5 and len(teamB) == 5:
    # Center the simulate button
    center_col1, center_col2, center_col3 = st.columns([1, 2, 1])
    with center_col2:
        if st.button("🚀 SIMULATE MATCHUP", use_container_width=True, type="primary"):
            with st.spinner("🏀 Simulating matchup... Analyzing player stats and predicting outcome..."):
                prob = simulate_matchup(teamA, teamB, df, model, feature_names)
            
            # Results section
            st.markdown("---")
            st.subheader("🎯 Matchup Results")
            
            # Enhanced results display
            col1, col2, col3 = st.columns([2, 1, 2])
            margin = (prob[1] - prob[0]) * 100
            
            with col1:
                st.markdown('<div class="probability-card">', unsafe_allow_html=True)
                st.metric(
                    label="Team A Win Probability",
                    value=f"{prob[1]*100:.1f}%",
                    delta=f"+{margin:.1f}%" if margin > 0 else f"{margin:.1f}%",
                    delta_color="normal"
                )
                st.progress(float(prob[1]))
                if prob[1] > 0.6:
                    st.success("🏆 Strong favorite!")
                elif prob[1] > 0.4:
                    st.info("⚖️ Competitive matchup")
                else:
                    st.warning("🔥 Underdog team")
                st.markdown('</div>', unsafe_allow_html=True)

            with col2:
                st.markdown("<br><br>", unsafe_allow_html=True)
                st.markdown("<h1 style='text-align: center; color: #666;'>VS</h1>", unsafe_allow_html=True)
                st.markdown("<br>", unsafe_allow_html=True)
                # Show odds
                if prob[1] > prob[0]:
                    odds = prob[1] / prob[0]
                    st.caption(f"Moneyline: +{int((odds - 1) * 100)}")
                else:
                    odds = prob[0] / prob[1]
                    st.caption(f"Moneyline: +{int((odds - 1) * 100)}")

            with col3:
                st.markdown('<div class="probability-card">', unsafe_allow_html=True)
                st.metric(
                    label="Team B Win Probability",
                    value=f"{prob[0]*100:.1f}%",
                    delta=f"{-margin:.1f}%" if margin > 0 else f"+{abs(margin):.1f}%",
                    delta_color="normal"
                )
                st.progress(float(prob[0]))
                if prob[0] > 0.6:
                    st.success("🏆 Strong favorite!")
                elif prob[0] > 0.4:
                    st.info("⚖️ Competitive matchup")
                else:
                    st.warning("🔥 Underdog team")
                st.markdown('</div>', unsafe_allow_html=True)

            # Enhanced visualization section
            st.subheader("📊 Matchup Analysis")
            viz_col1, viz_col2 = st.columns(2)
            
            with viz_col1:
                # Enhanced bar chart
                fig, ax = plt.subplots(figsize=(8, 4))
                teams = ['Team A', 'Team B']
                probabilities = [prob[1] * 100, prob[0] * 100]
                colors = ['#1D428A', '#C8102E']
                
                bars = ax.bar(teams, probabilities, color=colors, alpha=0.8)
                ax.set_ylabel('Win Probability (%)')
                ax.set_title('Win Probability Comparison', fontweight='bold')
                ax.set_ylim(0, 100)
                
                # Add value labels on bars
                for bar, value in zip(bars, probabilities):
                    ax.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 1,
                           f'{value:.1f}%', ha='center', va='bottom', fontweight='bold')
                
                st.pyplot(fig)

            with viz_col2:
                # Enhanced pie chart
                fig, ax = plt.subplots(figsize=(6, 6))
                colors = ['#1D428A', '#C8102E']
                wedges, texts, autotexts = ax.pie([prob[1], prob[0]], 
                                                labels=['Team A', 'Team B'], 
                                                autopct='%1.1f%%', 
                                                colors=colors, 
                                                startangle=90,
                                                textprops={'fontsize': 12, 'fontweight': 'bold'})
                
                # Improve autopct appearance
                for autotext in autotexts:
                    autotext.set_color('white')
                    autotext.set_fontweight('bold')
                
                ax.set_title('Win Probability Distribution', fontsize=14, fontweight='bold')
                st.pyplot(fig)

            # Enhanced team stats comparison
            st.subheader("📈 Detailed Team Comparison")
            try:
                teamA_stats = get_player_stats(teamA, df)
                teamB_stats = get_player_stats(teamB, df)
                avgA = teamA_stats.mean()
                avgB = teamB_stats.mean()
                
                # Create comparison dataframe
                stat_mapping = {
                    'PTS': 'Points',
                    'AST': 'Assists',
                    'STL': 'Steals', 
                    'BLK': 'Blocks'
                }
                if 'REB' in teamA_stats.columns:
                    stat_mapping['REB'] = 'Rebounds'
                elif 'TRB' in teamA_stats.columns:
                    stat_mapping['TRB'] = 'Rebounds'
                
                comparison_data = []
                for stat, label in stat_mapping.items():
                    if stat in avgA.index:
                        a_val = avgA[stat]
                        b_val = avgB[stat]
                        advantage = "Team A" if a_val > b_val else "Team B" if b_val > a_val else "Tie"
                        comparison_data.append({
                            'Statistic': label,
                            'Team A': f"{a_val:.1f}",
                            'Team B': f"{b_val:.1f}", 
                            'Advantage': advantage
                        })
                
                comparison_df = pd.DataFrame(comparison_data)
                
                # Display with colored advantages
                def color_advantage(val):
                    if val == "Team A":
                        return 'background-color: #1D428A; color: white;'
                    elif val == "Team B":
                        return 'background-color: #C8102E; color: white;'
                    else:
                        return 'background: rgb(14, 17, 23;'
                
                styled_df = comparison_df.style.applymap(color_advantage, subset=['Advantage'])
                st.dataframe(styled_df, use_container_width=True, hide_index=True)
                
            except Exception as e:
                st.error(f"Could not load detailed stats: {e}")

else:
    # Improved empty state
    st.markdown("---")
    empty_col1, empty_col2, empty_col3 = st.columns([1, 2, 1])
    with empty_col2:
        st.markdown('<div class="empty-state">', unsafe_allow_html=True)
        st.markdown('<div class="empty-state-icon">🎯</div>', unsafe_allow_html=True)
        st.info("""
        ## Ready to Simulate?
        
        **To unlock matchup simulation:**
        1. Select 5 players for Team A in the sidebar
        2. Select 5 players for Team B in the sidebar  
        3. Click the **SIMULATE MATCHUP** button
        
        *Build your dream teams and see who would win in an epic NBA showdown!*
        """)
        st.markdown("</div>", unsafe_allow_html=True)

# Footer with improved design
st.markdown("---")
st.markdown(
    "<div style='text-align: center; color: gray; font-size: 0.9rem; padding: 2rem;'>"
    "<strong>NBA Dream Team Simulator</strong> • Built with Streamlit • "
    "Create legendary matchups and discover basketball insights"
    "</div>",
    unsafe_allow_html=True
)