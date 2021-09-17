package eu.ase.proiect.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Comentariu;
import eu.ase.proiect.database.model.Recenzie;

public class ComentariuAdapter extends RecyclerView.Adapter<ComentariuAdapter.ComentariuViewHolder>  {

    private Context mContext;
    private List<Comentariu> mData;


    public ComentariuAdapter(Context mContext, List<Comentariu> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ComentariuAdapter.ComentariuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.rand_comentariu,parent,false);
        return new ComentariuAdapter.ComentariuViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentariuAdapter.ComentariuViewHolder holder, int position) {

        if (mData!=null) {
            if (mData.get(position).getuImg() == "" || mData.get(position).getuImg().isEmpty()) {
                holder.img_user.setImageResource(R.drawable.ic_uploading_photo);
            }
            Glide.with(mContext).load(mData.get(position).getuImg()).into(holder.img_user);
            holder.tv_name.setText(mData.get(position).getuNume());
            holder.tv_content.setText(mData.get(position).getContinut());
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

    public class ComentariuViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img_user;
        TextView tv_name,tv_content;

        public ComentariuViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_user_Comentariu);
            tv_name = itemView.findViewById(R.id.username_Comentariu);
            tv_content = itemView.findViewById(R.id.Comentariu_continut);
        }
    }
}
