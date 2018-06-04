package slg_aachen.de.shop_v01;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

public class SwatchAdapter extends RecyclerView.Adapter {
    private final LayoutInflater inflater;
    private final Swatch[] swatches;
    private int selectedCount;
    private boolean[] selected;

    public SwatchAdapter(LayoutInflater inflater, Swatch[] swatches) {
        this.inflater = inflater;
        this.swatches = swatches;
        selectedCount = 0;
        selected = new boolean[swatches.length];
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SwatchAdapter.ViewHolder(inflater.inflate(R.layout.view_list_row_swatches, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final View v = holder.itemView;
        final Swatch s = swatches[position];

        if (s != null) {
            final ImageView v1 = v.findViewById(R.id.ic1);
            final ImageView v2 = v.findViewById(R.id.ic2);
            final ImageView v3 = v.findViewById(R.id.ic3);
            final ImageView v4 = v.findViewById(R.id.ic4);
            final ImageView v5 = v.findViewById(R.id.ic5);
            final CheckBox box = v.findViewById(R.id.checkBox);

            v1.setColorFilter(s.getSwatch(0), PorterDuff.Mode.SRC_ATOP);
            v2.setColorFilter(s.getSwatch(1), PorterDuff.Mode.SRC_ATOP);
            v3.setColorFilter(s.getSwatch(2), PorterDuff.Mode.SRC_ATOP);
            v4.setColorFilter(s.getSwatch(3), PorterDuff.Mode.SRC_ATOP);
            v5.setColorFilter(s.getSwatch(4), PorterDuff.Mode.SRC_ATOP);

            box.setChecked(selected[position]);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    box.toggle();
                    selected[position] = box.isChecked();
                    if (box.isChecked()) {
                        selectedCount++;
                    } else {
                        selectedCount--;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return swatches.length;
    }

    public Swatch getItem(int pos) {
        return swatches[pos];
    }

    public Swatch[] getSelected() {
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

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View itemView) {
            super(itemView);
        }
    }
}