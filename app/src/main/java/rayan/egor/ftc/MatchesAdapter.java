package rayan.egor.ftc;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rayan.egor.ftc.engine.Match;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchViewHolder> {
    private final List<Match> data;

    public static class MatchViewHolder extends RecyclerView.ViewHolder {

        public ArrayList<ImageView> myScoreViews;
        public ArrayList<ImageView> enemyScoreViews;
        public final TextView myNickname;
        public final TextView enemyNickname;

        public MatchViewHolder(View itemView) {
            super(itemView);
            myNickname = itemView.findViewById(R.id.myNicknameTextView);
            enemyNickname = itemView.findViewById(R.id.enemyNicknameTextView);
            myScoreViews = new ArrayList<>();
            enemyScoreViews = new ArrayList<>();

            myScoreViews.add((ImageView) itemView.findViewById(R.id.scoreView1));
            myScoreViews.add((ImageView) itemView.findViewById(R.id.scoreView2));
            myScoreViews.add((ImageView) itemView.findViewById(R.id.scoreView3));

            enemyScoreViews.add((ImageView) itemView.findViewById(R.id.scoreView4));
            enemyScoreViews.add((ImageView) itemView.findViewById(R.id.scoreView5));
            enemyScoreViews.add((ImageView) itemView.findViewById(R.id.scoreView6));
        }
    }

    public MatchesAdapter(List<Match> data) {
        this.data = data;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_match, parent, false);
        view.setOnClickListener(MatchesListActivity.onClickListener);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        Match match = data.get(position);

        holder.myNickname.setText(match.getMyNickname());
        holder.enemyNickname.setText(match.getEnemyNickname());

        for (int id = 0; id < holder.myScoreViews.size(); ++id) {
            switch (match.getMyAnswers()[id]) {
                case Match.ANSWER_UNANSWERED:
                    holder.myScoreViews.get(id).setImageDrawable(new ColorDrawable(holder.itemView.getResources().getColor(R.color.colorUnanswered)));
                    break;
                case Match.ANSWER_CORRECT:
                    holder.myScoreViews.get(id).setImageDrawable(new ColorDrawable(holder.itemView.getResources().getColor(R.color.colorCorrect)));
                    break;
                case Match.ANSWER_WRONG:
                    holder.myScoreViews.get(id).setImageDrawable(new ColorDrawable(holder.itemView.getResources().getColor(R.color.colorWrong)));
                    break;
            }
        }
        for (int id = 0; id < holder.enemyScoreViews.size(); ++id) {
            switch (match.getEnemyAnswers()[id]) {
                case Match.ANSWER_UNANSWERED:
                    holder.enemyScoreViews.get(id).setImageDrawable(new ColorDrawable(holder.itemView.getResources().getColor(R.color.colorUnanswered)));
                    break;
                case Match.ANSWER_CORRECT:
                    holder.enemyScoreViews.get(id).setImageDrawable(new ColorDrawable(holder.itemView.getResources().getColor(R.color.colorCorrect)));
                    break;
                case Match.ANSWER_WRONG:
                    holder.enemyScoreViews.get(id).setImageDrawable(new ColorDrawable(holder.itemView.getResources().getColor(R.color.colorWrong)));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
