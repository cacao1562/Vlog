package kr.co.valuesys.vlog.mobile.Fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.FragmentCalendarBinding;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private ArrayList<CalendarDay> mVideosDate;

    public static CalendarFragment newInstance() { return new CalendarFragment(); }

    private int minYear;
    private int minMonth;
    private int maxYear;
    private int maxMonth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new SetDrawVideoDate().execute();
//        binding.calendarView.state().edit()
//                .setFirstDayOfWeek(Calendar.SUNDAY)
//                .setMinimumDate(CalendarDay.from(2019, 0, 1))
//                .setMaximumDate(CalendarDay.from(2019, 11, 31))
//                .setCalendarDisplayMode(CalendarMode.MONTHS)
//                .commit();
//
//        binding.calendarView.addDecorators(new SundayDecorator(),
//                                           new SaturdayDecorator(),
//                                           new ToDayDecorator(),
//                                           new EventDecorator(R.color.black, mVideosDate),
//                                           new NewDecorator(getActivity()) );

// 날짜 클릭 이벤트
        binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                if (date.equals(CalendarDay.today())) {
//                    getActivity().finish();
                }
            }
        });

        binding.backButton.setOnClickListener(v -> {
            getActivity().finish();
        });

//        binding.calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);

    }

// 비디오 생성 date 리스트에 넣어서 달력에 표시하기 위해
    private ArrayList<CalendarDay> getVideosDate() {

        ArrayList<CalendarDay> dateList =  new ArrayList<>();
        ArrayList<VideoInfo> videoInfos = VideoInfo.getVideo(getActivity(), null);

        if (videoInfos != null) {

            for (VideoInfo info : videoInfos) {

                dateList.add(CalendarDay.from(info.getDate()));

            }
        }

        return dateList;
    }


// 일요일 빨간색 글씨로 표시
    public class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

// 토요일 파린색 글씨로 표시
    public class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

// 오늘 날짜 초록색으로 표시
    public class ToDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public ToDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.GREEN));
        }

        /**
         */
        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }


// param에 날짜 리스트를 넣으면 해당 날짜에 점 표시
    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }


    public class NewDecorator implements DayViewDecorator {

        private Drawable drawable;

        CalendarDay currentDay = CalendarDay.from(new Date());

        public NewDecorator(Activity context) {
            drawable = ContextCompat.getDrawable(context, R.drawable.first_day_month);

        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(currentDay);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
        }
    }

    private class SetDrawVideoDate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            mVideosDate = getVideosDate();

            minYear = mVideosDate.get(mVideosDate.size()-1).getYear();
            minMonth = mVideosDate.get(mVideosDate.size()-1).getMonth();
            maxYear = mVideosDate.get(0).getYear();
            maxMonth = mVideosDate.get(0).getMonth();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            binding.calendarView.state().edit()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setMinimumDate(CalendarDay.from(minYear, minMonth, 1))
                    .setMaximumDate(CalendarDay.from(maxYear, maxMonth, 31))
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();

            binding.calendarView.addDecorators(new SundayDecorator(),
                    new SaturdayDecorator(),
                    new ToDayDecorator(),
                    new EventDecorator(R.color.black, mVideosDate),
                    new NewDecorator(getActivity()) );

        }
    }

}
