package com.example.ninja;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.List;

//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
//
//    private List<User> users;
//
//    public LeaderboardAdapter(List<User> users) {
//        this.users = users;
//    }
//
//    @NonNull
//    @Override
//    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_leaderboard_row, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull LeaderboardAdapter.ViewHolder holder, int position) {
//        User user = users.get(position);
//        int rank = position + 4; // Since top 3 are in podium
//
//        holder.rankText.setText(String.valueOf(rank));
//        holder.nameText.setText(user.getUsername());
//        holder.scoreText.setText(user.getScore() + " points");
//    }
//
//    @Override
//    public int getItemCount() {
//        return users.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView rankText, nameText, scoreText;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            rankText = itemView.findViewById(R.id.rankText);
//            nameText = itemView.findViewById(R.id.nameText);
//            scoreText = itemView.findViewById(R.id.scoreText);
//        }
//    }
////}
//public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
//    private List<User> users;
//
//    public LeaderboardAdapter(List<User> users) {
//        this.users = users;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView rankText, nameText, scoreText;
//        ImageView profileImage;
//
//        public ViewHolder(View view) {
//            super(view);
//            rankText = view.findViewById(R.id.rankText);
//            nameText = view.findViewById(R.id.nameText);
//            scoreText = view.findViewById(R.id.scoreText);
//            profileImage = view.findViewById(R.id.profileImage);
//        }
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_leaderboard_row, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        User user = users.get(position);
//        holder.rankText.setText(String.valueOf(position + 4)); // since top 3 are outside
//        holder.nameText.setText(user.getUsername());
//        holder.scoreText.setText(user.getScore() + " points");
//
//        Glide.with(holder.itemView.getContext())
//                .load(user.getProfilePhotoUrl())
//                .placeholder(R.drawable.baseline_person)
//                .into(holder.profileImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return users.size();
//    }
//}

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<User> userList;

    public LeaderboardAdapter(List<User> userList) {
        this.userList = userList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText, nameText, scoreText;
        ImageView profileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rankText);
            nameText = itemView.findViewById(R.id.nameText);
            scoreText = itemView.findViewById(R.id.scoreText);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }

    @NonNull
    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.rankText.setText(String.valueOf(position + 4));
        holder.nameText.setText(user.getUsername());
        holder.scoreText.setText(user.getScore() + " միավոր");

        String photoUrl = user.getProfilePhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.baseline_person)
                    .error(R.drawable.baseline_person)
                    .transform(new CircleCrop())
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.baseline_person);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
