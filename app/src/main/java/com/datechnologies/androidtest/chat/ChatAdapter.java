package com.datechnologies.androidtest.chat;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.datechnologies.androidtest.R;
import com.datechnologies.androidtest.api.ChatLogMessageModel;
import com.datechnologies.androidtest.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A recycler view adapter used to display chat log messages in {@link ChatActivity}.

 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private List<ChatLogMessageModel> chatLogMessageModelList;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    public ChatAdapter()
    {
        chatLogMessageModelList = new ArrayList<>();
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public void setChatLogMessageModelList(List<ChatLogMessageModel> chatLogMessageModelList)
    {
        this.chatLogMessageModelList = chatLogMessageModelList;
        notifyDataSetChanged();
    }

    //==============================================================================================
    // RecyclerView.Adapter Methods
    //==============================================================================================

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_chat, parent, false);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder viewHolder, int position)
    {
        ChatLogMessageModel chatLogMessageModel = chatLogMessageModelList.get(position);

        viewHolder.messageTextView.setText(chatLogMessageModel.message);
        viewHolder.usernameTextView.setText(chatLogMessageModel.username);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            Bitmap bitmap;
            bitmap = Utils.fetchImage(chatLogMessageModel.avatarUrl);

            new Handler(Looper.getMainLooper()).post(() -> {
                if(bitmap != null){
                    viewHolder.avatarImageView.setImageBitmap(bitmap);
                    viewHolder.avatarImageView.setClipToOutline(true);
                }
            });
        });
    }

    @Override
    public int getItemCount()
    {
        return chatLogMessageModelList.size();
    }

    //==============================================================================================
    // ChatViewHolder Class
    //==============================================================================================

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        ImageView avatarImageView;
        TextView messageTextView;
        TextView usernameTextView;

        public ChatViewHolder(View view)
        {
            super(view);
            avatarImageView = view.findViewById(R.id.avatarImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
            usernameTextView = view.findViewById(R.id.usernameTextView);
        }
    }

}
