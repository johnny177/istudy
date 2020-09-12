package com.nnoboa.istudy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.blog.blogUtils.BlogItem;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class BlogAdapter extends ArrayAdapter<BlogItem> {
    public BlogAdapter(@NonNull Context context, ArrayList<BlogItem> blogItems) {
        super(context, 0, blogItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View blogView = convertView;

        AndroidThreeTen.init(getContext());

        if (blogView == null) {
            blogView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.blog, parent, false);
        }

        BlogItem blogItems = getItem(position);

        TextView authCred = blogView.findViewById(R.id.auth_name);
        TextView author = blogView.findViewById(R.id.author);
        TextView title = blogView.findViewById(R.id.title);
        TextView time = blogView.findViewById(R.id.time);

        authCred.setText(blogItems.getmAuthor().substring(0, 1));
        author.setText(blogItems.getmAuthor());
        title.setText(blogItems.getmTitle());
        String newDate = convertLongToDate(formatString(blogItems.getmDatePublished()));
        time.setText(newDate);

        GradientDrawable nameCircle = (GradientDrawable) authCred.getBackground();

        int nameColor = getRandomColor();

        nameCircle.setColor(nameColor);

        return blogView;
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.argb(180, random.nextInt(200), random.nextInt(210), random.nextInt(250));
    }

    private String formatString(String time) {
        int last = time.indexOf("T");
        time = time.substring(0, last);
        return time;
    }
    private String convertLongToDate(String date){
//        Date date1 = new Date(date);
//
//        DateFormat dateFormat = new SimpleDateFormat("dd:MM:YYYY");
//
//        String date2 = dateFormat.format(date1);

        String[] newDate = date.split("-");

        int month = Integer.parseInt(newDate[1]);
        String month_name;

        switch (month){
            case 01 | 1:
                month_name = "January";
                break;
            case 02 | 2:
                month_name = "February";
                break;
            case 03 |3:
                month_name = "March";
                break;
            case 04 | 4:
                month_name = "April";
                break;

            case 05 | 5:
                month_name = "May";
                break;
            case 06 | 6:
                month_name = "June";
                break;

            case 07 | 7:
                month_name = "July";
                break;

            case 8 :
                month_name = "August";
                break;

            case 9 :
                month_name = "September";
                break;

            case 10 | 10:
                month_name = "October";
                break;

            case 11 :
                month_name = "November";
                break;

            case 12 :
                month_name = "December";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }

        String newDay;
        String day = newDate[2];
        switch (day){
            case "01":
                newDay = "1st";
                break;

            case "21":
                newDay = "21st";
                break;

            case "31":
                newDay = "31st";
                break;

            case "02":
                newDay = "2nd";
                break;

            case "03":
                newDay = "3rd";
                break;

            case "22":
                newDay = "22nd";
                break;

            case "23":
                newDay = "23rd";
                break;
            default:
                newDay = day+"th";

        }

        return newDay+" "+month_name+", "+newDate[0];

    }


    public long Millis(String time){
        long milli = 0;
        try{


            time = time.replace(" ","T").replace("/","-");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);

            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();

            milli = offsetDateTime.toInstant().toEpochMilli();
            Log.i("Millis"," "+milli);}
        catch (org.threeten.bp.format.DateTimeParseException e ){
//            String DateTime = date+" "+time+":00";
//
//
////            DateTime = DateTime.replace(" ","T").replace("/","-");
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//            LocalDateTime localDateTime = LocalDateTime.parse(DateTime, dateTimeFormatter);
//
//            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
//            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
//
//            milli = offsetDateTime.toInstant().toEpochMilli();
//            Log.i("Millis"," "+milli);
        Log.d("TimeTo Milli :","failed",e);
        }
        return milli;
    }
}
