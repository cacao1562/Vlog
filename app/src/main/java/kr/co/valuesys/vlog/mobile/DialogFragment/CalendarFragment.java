package kr.co.valuesys.vlog.mobile.DialogFragment;

import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.FragmentCalendarBinding;

public class CalendarFragment extends DialogFragment {

    private FragmentCalendarBinding binding;
    private ArrayList<CalendarDay> mVideosDate;

    public static CalendarFragment newInstance() { return new CalendarFragment(); }


    private SetDrawVideoDate setDrawVideoDate;

    private CommonInterface.OnCallbackToMain mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (CommonInterface.OnCallbackToMain) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
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

        setDrawVideoDate = new SetDrawVideoDate(this);
        setDrawVideoDate.execute();

        binding.calendarView.addDecorators(new SundayDecorator(),
                new SaturdayDecorator(),
                new ToDayDecorator() );

// 날짜 클릭 이벤트

        binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                LogUtil.d("cal", "selected = " + date );

                binding.calendarView.setDateSelected(date, false);

                if (mVideosDate != null) {

                    if (mVideosDate.contains(date)) {

                        MobileApplication.getContext().setmSelectDay(date);
//                        getActivity().finish();

                        dismiss();
                        mListener.oncallbackMain(2);
                    }
                }

            }

        });

        binding.backButton.setOnClickListener(v -> {
//            getActivity().finish();
            dismiss();
        });

        binding.calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (setDrawVideoDate.getStatus() == AsyncTask.Status.RUNNING) {
            setDrawVideoDate.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // 비디오 생성 date 리스트에 넣어서 달력에 표시하기 위해
    private ArrayList<CalendarDay> getVideosDate() {

        ArrayList<CalendarDay> dateList =  new ArrayList<>();
        ArrayList<VideoInfo> videoInfos = VideoInfo.getVideo(getActivity(), false, null);

        if (videoInfos != null) {

            Calendar cal = Calendar.getInstance();
            CalendarDay prev = null;

            for (VideoInfo info : videoInfos) {

                cal.setTime(info.getDate());

                CalendarDay day = CalendarDay.from(cal);

// 같은 날짜는 추가 안함
                if (!day.equals(prev)) {
                    dateList.add(day);
                }

                prev = day;

            }
        }

        LogUtil.d("date list ", " size = " + dateList.size() );

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
//        public void setDate(Date date) {
//            this.date = CalendarDay.from(date);
//        }
    }


// param에 날짜 리스트를 넣으면 해당 날짜에 동그라미 표시
    public static class EventDecorator implements DayViewDecorator {

        private final int color;
//        private final HashSet<CalendarDay> dates;
        private final Collection<CalendarDay> dates;
        private Drawable drawable;

        public EventDecorator(Activity context, int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
//            this.dates.add(CalendarDay.today() );
            this.drawable = ContextCompat.getDrawable(context, R.drawable.day_border);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            //점 표시
//            view.addSpan(new DotSpan(5, color));
            view.setSelectionDrawable(drawable);
        }
    }

// 오늘 날짜에 검은 테두리 표시
//    public class NewDecorator implements DayViewDecorator {
//
//        private Drawable drawable;
//
//        CalendarDay currentDay = CalendarDay.from(Calendar.getInstance());
//
//        public NewDecorator(Activity context) {
//            drawable = ContextCompat.getDrawable(context, R.drawable.first_day_month);
//
//        }
//
//        @Override
//        public boolean shouldDecorate(CalendarDay day) {
//            return day.equals(currentDay);
//        }
//
//        @Override
//        public void decorate(DayViewFacade view) {
//            view.setSelectionDrawable(drawable);
//        }
//    }

    private static class SetDrawVideoDate extends AsyncTask<Void, Void, Void> {

        private int minYear;
        private int minMonth;
        private int maxYear;
        private int maxMonth;
        private WeakReference<CalendarFragment> wrFragment;
        private CalendarFragment fragment;

        SetDrawVideoDate(CalendarFragment calendarFragment) {
            this.wrFragment = new WeakReference<>(calendarFragment);
            this.fragment = wrFragment.get();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (fragment == null || fragment.getActivity() == null || fragment.getActivity().isFinishing() || isCancelled()) {
                return null;
            }

            fragment.mVideosDate = fragment.getVideosDate();

            if (fragment.mVideosDate != null && fragment.mVideosDate.size() > 0 ) {

// 리스트가 내림차순으로 정렬되어있어서 앞에가 높은날짜
                minYear = fragment.mVideosDate.get(fragment.mVideosDate.size()-1).getYear();
                minMonth = fragment.mVideosDate.get(fragment.mVideosDate.size()-1).getMonth();
                maxYear = fragment.mVideosDate.get(0).getYear();
                maxMonth = fragment.mVideosDate.get(0).getMonth();

            }else {

                Calendar cal = Calendar.getInstance();
                minYear = cal.get(Calendar.YEAR);
                minMonth = 0;
                maxYear = cal.get(Calendar.YEAR);
                maxMonth = 11;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (fragment == null || fragment.getActivity() == null || fragment.getActivity().isFinishing()) {
                return;
            }

            fragment.binding.calendarView.state().edit()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setMinimumDate(CalendarDay.from(minYear, minMonth, 1))
                    .setMaximumDate(CalendarDay.from(maxYear, 11, 31))
//                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit();

            fragment.binding.calendarView.addDecorators(
                    new EventDecorator(fragment.getActivity(), R.color.black, fragment.mVideosDate) );

        }
    }

}
