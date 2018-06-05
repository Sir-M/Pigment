package slg_aachen.de.shop_v01;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.Arrays;

public class SwatchAdapter extends RecyclerView.Adapter<SwatchAdapter.CustomViewHolder> {
    private final Swatch[] swatches;
    private boolean[] selected;
    private int selectedCount;
    private OnItemClickListener listener;
    // private SparseBooleanArray itemStateArray;

    SwatchAdapter(Swatch[] swatches) {
        this.swatches = swatches;
        selected = new boolean[swatches.length];
        selectedCount = 0;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //itemStateArray = new SparseBooleanArray();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_row_swatches, parent, false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        final View v = holder.itemView;
        final Swatch s = swatches[position];

        if (s != null) {

            holder.v1.setColorFilter(s.getSwatch(0), PorterDuff.Mode.SRC_ATOP);
            holder.v2.setColorFilter(s.getSwatch(1), PorterDuff.Mode.SRC_ATOP);
            holder.v3.setColorFilter(s.getSwatch(2), PorterDuff.Mode.SRC_ATOP);
            holder.v4.setColorFilter(s.getSwatch(3), PorterDuff.Mode.SRC_ATOP);
            holder.v5.setColorFilter(s.getSwatch(4), PorterDuff.Mode.SRC_ATOP);

            holder.box.setChecked(selected[position]);

            // holder.box.setChecked(itemStateArray.get(position, false));
            // holder.box.setChecked(selected[position]);

            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.box.toggle();
                    selected[position] = holder.box.isChecked();
                    if (holder.box.isChecked()) {
                        selectedCount++;
                    } else {
                        selectedCount--;
                    }
                }
            });*/
        } else {
            Log.e("Error SwatchAdapter", "swatches at " + position + " == null");
        }
    }

    @Override
    public int getItemCount() {
        return swatches.length;
    }

    public Swatch getItem(int pos) {
        return swatches[pos];
    }

    public Swatch[] getSelectedSwatches() {
        Swatch[] array = new Swatch[selectedCount];

        int iA = 0;
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                array[iA] = swatches[i];
                iA++;
            }
        }

        return array;
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> pos = new List<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                pos.append(i);

            }
        }
        return pos;
    }

    public void setItemClickListener(final OnItemClickListener listener) {
        this.listener = listener;
    }

    public int getSelectedCount() {
        return selectedCount;
    }

    public void uncheckAll() {
        selectedCount = 0;
        Arrays.fill(selected, false);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView v1;
        ImageView v2;
        ImageView v3;
        ImageView v4;
        ImageView v5;
        CheckBox box;

        private CustomViewHolder(View v) {
            super(v);
            v1 = v.findViewById(R.id.ic1);
            v2 = v.findViewById(R.id.ic2);
            v3 = v.findViewById(R.id.ic3);
            v4 = v.findViewById(R.id.ic4);
            v5 = v.findViewById(R.id.ic5);
            box = v.findViewById(R.id.checkBox);
            // v.setOnClickListener(this);
            itemView.setOnClickListener(this);
            box.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (!selected[adapterPosition]) {
                box.setChecked(true);
                selected[adapterPosition] = true;
                selectedCount++;

                //   ((MainActivity) getActivity()).setSelectedCheckBoxes(0);


            } else {
                box.setChecked(false);
                selected[adapterPosition] = false;
                selectedCount--;
            }

            if (listener != null) {
                listener.onItemClick(v, getAdapterPosition());
            }
        }
    }
    // public void uncheckAll(){
    //}
}