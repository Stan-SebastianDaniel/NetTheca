package eu.ase.proiect.util;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Recenzie;

public class RecenzieAdapter extends RecyclerView.Adapter<RecenzieAdapter.RecenzieViewHolder> {

    private Context mContext;
    private List<Recenzie> mData;


    public RecenzieAdapter(Context mContext, List<Recenzie> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecenzieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.rand_recenzii,parent,false);
        return new RecenzieViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecenzieViewHolder holder, int position) {

        if (mData!=null) {
            if (mData.get(position).getuImg() == "" || mData.get(position).getuImg().isEmpty()) {
                holder.img_user.setImageResource(R.drawable.ic_uploading_photo);
            }
            Glide.with(mContext).load(mData.get(position).getuImg()).into(holder.img_user);
            holder.tv_name.setText(mData.get(position).getuNume());
            holder.tv_content.setText(mData.get(position).getContinut());
            // holder.tv_date.setText(mData.get(position).getTimestamp());
            holder.tv_date.setText("");
            holder.scorul.setRating(mData.get(position).getScore());
        }

    }

    @Override
    public int getItemCount() {
        if (mData == null){
            return 0;
        }
        else {
            return mData.size();
        }
    }

        public class RecenzieViewHolder extends RecyclerView.ViewHolder{

            CircleImageView img_user;
            TextView tv_name,tv_content,tv_date;
            RatingBar scorul;

            public RecenzieViewHolder(View itemView) {
                super(itemView);
                img_user = itemView.findViewById(R.id.img_user_recenzie);
                tv_name = itemView.findViewById(R.id.username_recenzie);
                tv_content = itemView.findViewById(R.id.recenzie_continut);
                tv_date = itemView.findViewById(R.id.recenzie_data);
                scorul=itemView.findViewById(R.id.ratingBar_rand_review);
            }
        }

        private String timestampToString(long time) {

            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(time);
            String date = DateFormat.format("hh:mm",calendar).toString();
            return date;


        }

}
