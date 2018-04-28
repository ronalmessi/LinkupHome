package com.ihomey.linkuphome.widget.toprightmenu;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihomey.linkuphome.R;

import java.util.List;

/**
 * Author：Bro0cL on 2016/12/26.
 */
public class TRMenuAdapter extends RecyclerView.Adapter<TRMenuAdapter.TRMViewHolder> {
    private Context mContext;
    private List<MenuItem> menuItemList;
    private boolean showIcon;
    private TopRightMenu mTopRightMenu;
    private TopRightMenu.OnMenuItemClickListener onMenuItemClickListener;

    public TRMenuAdapter(Context context, TopRightMenu topRightMenu, List<MenuItem> menuItemList, boolean show) {
        this.mContext = context;
        this.mTopRightMenu = topRightMenu;
        this.menuItemList = menuItemList;
        this.showIcon = show;
    }

    public void setData(List<MenuItem> data){
        menuItemList = data;
        notifyDataSetChanged();
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
        notifyDataSetChanged();
    }

    @Override
    public TRMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.toolbar_popup_menu_list, parent, false);
        return new TRMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TRMViewHolder holder, int position) {
        final MenuItem menuItem = menuItemList.get(position);
        if (showIcon){
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageDrawable(newSelector(mContext,menuItem.getNormalIcon(),menuItem.getSelectIcon()));
        }else{
            holder.icon.setVisibility(View.GONE);
        }

        holder.text.setText(menuItem.getTextRes());
        holder.text.setTextColor(createColorStateList( Color.parseColor("#5B5B5B"), Color.parseColor("#FF6666")));


        final int pos = holder.getAdapterPosition();
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuItemClickListener != null) {
                    mTopRightMenu.dismiss();
                    onMenuItemClickListener.onMenuItemClick(pos,menuItem.getId());
                }
            }
        });
    }


    /** 对TextView设置不同状态时其文字颜色。 */
    private ColorStateList createColorStateList(int normal, int pressed) {
        int[] colors = new int[] { pressed, normal };
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_pressed};
        states[1] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /** 设置Selector。 */
    public static StateListDrawable newSelector(Context context, int idNormal, int idPressed) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);

        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_pressed}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        // View.EMPTY_STATE_SET
        bg.addState(new int[] {}, normal);
        return bg;
    }




    @Override
    public int getItemCount() {
        return menuItemList == null ? 0 : menuItemList.size();
    }

    class TRMViewHolder extends RecyclerView.ViewHolder{
        ViewGroup container;
        ImageView icon;
        TextView text;

        TRMViewHolder(View itemView) {
            super(itemView);
            container = (ViewGroup) itemView;
            icon = (ImageView) itemView.findViewById(R.id.trm_menu_item_icon);
            text = (TextView) itemView.findViewById(R.id.trm_menu_item_text);
        }
    }

    public void setOnMenuItemClickListener(TopRightMenu.OnMenuItemClickListener listener){
        this.onMenuItemClickListener = listener;
    }
}
