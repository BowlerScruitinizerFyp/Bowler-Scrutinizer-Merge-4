package application.scrutinizer.bowler.bowlerscrutinizer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }
    public int [] slide_images = {

            R.drawable.bowling_icon,
            R.drawable.mycamera_icon,
            R.drawable.myresults_icon
    };

    public String [] slide_headings = {

            "ANALYSE","CAPTURING","RECORD"
    };

    public String [] slide_descs = {
            "Bowler Scrutinizer provide keen operations to analyse the angle of bowler that moulds you to make better bowler. ",
            "Capture your ultimate images of bowling angle that helps you to identify your errors of bowling in every way. ",
            "Provide you such functions in a way to record your past bowling result with a good remedy solution for you. "
    };



    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDes = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDes.setText(slide_descs[position]);



        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((RelativeLayout)object);
    }
}
