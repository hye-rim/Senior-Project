package com.onpuri.Activity.SideTab.Act;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Home.Fragment.HomeSentenceFragment;
import com.onpuri.Activity.SideTab.Act.ActTest.UserMyActTestFragment;
import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.RecyclerItemClickListener;
import com.onpuri.R;

import java.util.ArrayList;

import static com.onpuri.R.drawable.divider_dark;

/**
 * Created by kutemsys on 2016-05-26.
 */
public class UserMyActFragment extends Fragment {
    private static final String TAG = "UserMyActFragment";
    private static View view;
    private static final String ALL = "0";
    private TabHost mTabHost;

    ArrayList<String> listNew, listNewNum, listNewId;
    ArrayList<String> listRecord, listRecordNum,  listRecordId;
    ArrayList<String> listTrans, listTransNum, listTransId;
    ArrayList<String> listTest, listTestNum, listTestPercent, listTestKinds;

    private RecyclerView mRecyclerNew, mRecyclerRecord, mRecyclerTrans, mRecyclerTest;
    private RecyclerView.Adapter mNewAdapter, mRecordAdapter, mTransAdapter, mTestAdapter;

    private TextView tv_userId, tv_userInfo_NewRec, tv_userInfo_TransTest;

    protected RecyclerView.LayoutManager mLayoutManager;

    private FrameLayout mItemFrame;
    private FragmentManager mFragmentManager;

    private Boolean isNullNew, isNullRecord, isNullTrans, isNullTest;

    private workerAct mworker_act;

    private int cntNew, cntRecord, cntTrans, cntTest;

    FragmentManager fm;

    private ViewPager viewPager;

    public static UserMyActFragment newInstance() {
        UserMyActFragment fragment = new UserMyActFragment();
        return fragment;
    }

    public UserMyActFragment() {

        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_my_act, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);

        isNullNew = false;
        isNullRecord = false;
        isNullTrans = false;
        isNullTest = false;

        fm = getActivity().getSupportFragmentManager();

        Bundle extra = getArguments();
        String userId = null;
        userId = extra.getString("ActId");

        tv_userId = (TextView)view.findViewById(R.id.tv_act_name);
        tv_userInfo_NewRec = (TextView)view.findViewById(R.id.tv_act_info_new_rec);
        tv_userInfo_TransTest = (TextView)view.findViewById(R.id.tv_act_info_trans_test);

        tv_userId.setText("     " + userId + " 님");

        mFragmentManager = getFragmentManager();
        mItemFrame = (FrameLayout)view.findViewById(R.id.my_act_item);
        mTabHost = (TabHost) view.findViewById(R.id.my_act_tab);

        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_1")
                .setIndicator("등록문장")
                .setContent(R.id.tab_act_new));

        mTabHost.addTab(mTabHost.newTabSpec("tab_2")
                .setIndicator("녹음문장")
                .setContent(R.id.tab_act_record));

        mTabHost.addTab(mTabHost.newTabSpec("tab_3")
                .setIndicator("해석문장")
                .setContent(R.id.tab_act_translate));
        mTabHost.addTab(mTabHost.newTabSpec("tab_4")
                .setIndicator("출제목록")
                .setContent(R.id.tab_act_test));

        setTabColor(); //탭 색상 지정
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { //탭 색상 변경
            @Override
            public void onTabChanged(String tabId) {
                setTabColor();
            }
        });

        loadActData();

        tv_userInfo_NewRec.setText("등록 : " + cntNew + "\n\n녹음 : " + cntRecord );
        tv_userInfo_TransTest.setText("해석 : " + cntTrans + "\n\n시험 : " + cntTest  );
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);

        //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerNew = (RecyclerView) view.findViewById(R.id.recycle_act_new);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNewAdapter = new MyActNewAdapter(getActivity().getApplicationContext(),listNew,mRecyclerNew);
        mRecyclerNew.setAdapter(mNewAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerNew.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerNew.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerNew, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullNew)
                            Toast.makeText(getActivity().getApplicationContext(), "문장을 등록해보세요.", Toast.LENGTH_SHORT).show();
                        else if( position >=  listNewNum.size()){

                        }
                        else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();

                            Bundle args = new Bundle();
                            args.putString("sen", listNew.get(position));
                            args.putString("sen_num", listNewNum.get(position));
                            args.putString("id", listNewId.get(position));

                            homeSentenceFragment.setArguments(args);
                            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
                            fm.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();

                            viewPager.setCurrentItem(0);
                        }
                    }

                }));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerRecord = (RecyclerView) view.findViewById(R.id.recycle_act_record);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecordAdapter = new MyActRecordAdapter(listRecord,mRecyclerRecord);
        mRecyclerRecord.setAdapter(mRecordAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerRecord.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerRecord.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerRecord, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullRecord)
                            Toast.makeText(getActivity().getApplicationContext(), "문장 녹음을 등록해보세요.", Toast.LENGTH_SHORT).show();
                        else if( position >=  listRecordNum.size()){

                        }
                        else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();

                            Bundle args = new Bundle();
                            args.putString("sen", listRecord.get(position));
                            args.putString("sen_num", listRecordNum.get(position));
                            args.putString("id", listRecordId.get(position));

                            homeSentenceFragment.setArguments(args);

                            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
                            fm.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();

                            viewPager.setCurrentItem(0);
                        }
                    }

                }));

        //Set Word Adapter for Word RecyclerView (NoteTab)
        mRecyclerTrans = (RecyclerView) view.findViewById(R.id.recycle_act_translate);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransAdapter = new MyActTranslateAdapter(listTrans,mRecyclerTrans);
        mRecyclerTrans.setAdapter(mTransAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerTrans.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerTrans.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerTrans, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullTrans)
                            Toast.makeText(getActivity().getApplicationContext(), "문장 번역을 등록해보세요.", Toast.LENGTH_SHORT).show();
                        else if( position >= listTransNum.size()){

                        }
                        else{
                            HomeSentenceFragment homeSentenceFragment = new HomeSentenceFragment();

                            Bundle args = new Bundle();
                            args.putString("sen", listTrans.get(position));
                            args.putString("sen_num", listTransNum.get(position));
                            args.putString("id", listTransId.get(position));

                            homeSentenceFragment.setArguments(args);

                            FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(0);
                            fm.popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fm.beginTransaction()
                                    .replace(R.id.root_home, homeSentenceFragment)
                                    .addToBackStack(null)
                                    .commit();
                            fm.executePendingTransactions();

                            viewPager.setCurrentItem(0);
                        }
                    }

                }));

    //Set Sentence Adapter for Sentence RecyclerView (NoteTab)
        mRecyclerTest = (RecyclerView) view.findViewById(R.id.recycle_act_test);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTestAdapter = new MyActTestAdapter(getActivity().getApplicationContext(),listTest, listTestPercent, listTestKinds ,mRecyclerTest);
        mRecyclerTest.setAdapter(mTestAdapter);// Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerTest.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        mRecyclerTest.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), mRecyclerTest, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(isNullTest)
                            Toast.makeText(getActivity().getApplicationContext(), "시험을 출제해보세요.", Toast.LENGTH_SHORT).show();
                        else if( ALL.compareTo(listTestKinds.get(position).toString()) == 0){
                            Toast.makeText(getActivity().getApplicationContext(), "전체 대상 시험입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if( position >= listTestNum.size()){

                        }
                        else{
                            UserMyActTestFragment userMyActTestFragment = new UserMyActTestFragment();

                            Bundle args = new Bundle();
                            args.putString("test_title", listTest.get(position));
                            args.putString("test_percent", listTestPercent.get(position));
                            args.putString("test_num", listTestNum.get(position));

                            userMyActTestFragment.setArguments(args);

                            fm.beginTransaction()
                                    .add(R.id.containerView, userMyActTestFragment)
                                    .addToBackStack("fragBack")
                                    .commit();
                            fm.executePendingTransactions();
                        }
                    }

                }));
        return view;
    }

    private void initActData() {
        listNew = new ArrayList<String>();
        listNewNum = new ArrayList<String>();
        listNewId = new ArrayList<String>();

        listRecord = new ArrayList<String>();
        listRecordNum = new ArrayList<String>();
        listRecordId = new ArrayList<String>();

        listTrans = new ArrayList<String>();
        listTransNum = new ArrayList<String>();
        listTransId = new ArrayList<String>();

        listTest = new ArrayList<String>();
        listTestNum = new ArrayList<String>();
        listTestPercent = new ArrayList<String>();
        listTestKinds = new ArrayList<String>();
    }

    public void loadActData(){
        initActData();

        if (mworker_act != null && mworker_act.isAlive()) {  //이미 동작하고 있을 경우 중지
            mworker_act.interrupt();
        }
        mworker_act = new workerAct(true);
        mworker_act.start();
        try {
            mworker_act.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = 0;

        if(mworker_act.getActNewSentence().arrSentence != null) {
            isNullNew = false;
            while (i < mworker_act.getActNewSentence().arrSentence.size()) {
                listNew.add(mworker_act.getActNewSentence().arrSentence.get(i));
                listNewNum.add(mworker_act.getActNewSentence().arrSentenceNum.get(i));
                listNewId.add(mworker_act.getActNewSentence().arrSentenceId.get(i));

                Log.d(TAG, "문장등록 =>" + mworker_act.getActNewSentence().arrSentence.get(i));
                i++;
            }
        }
        if(listNew.isEmpty()){
            listNew.add("문장을 등록해보세요.");
            isNullNew = true;
        }else{
            listNew.add("");
        }

        i = 0;
        if(mworker_act.getActRecSentence().arrSentence != null) {
            isNullRecord = false;
            while (i < mworker_act.getActRecSentence().arrSentence.size()) {
                listRecord.add(mworker_act.getActRecSentence().arrSentence.get(i));
                listRecordNum.add(mworker_act.getActRecSentence().arrSentenceNum.get(i));
                listRecordId.add(mworker_act.getActRecSentence().arrSentenceId.get(i));

                Log.d(TAG, "문장녹음 =>" + mworker_act.getActRecSentence().arrSentence.get(i));
                i++;
            }
        }
        if(listRecord.isEmpty()){
            isNullRecord = true;
            listRecord.add("문장 녹음을 해보세요.");
        }else{
            listRecord.add("");
        }

        i = 0;
        if(mworker_act.getActTransSentence().arrSentence != null) {
            isNullTrans = false;
            while (i < mworker_act.getActTransSentence().arrSentence.size()) {
                listTrans.add(mworker_act.getActTransSentence().arrSentence.get(i));
                listTransNum.add(mworker_act.getActTransSentence().arrSentenceNum.get(i));
                listTransId.add(mworker_act.getActTransSentence().arrSentenceId.get(i));

                Log.d(TAG, "문장번역 =>" + mworker_act.getActTransSentence().arrSentence.get(i));
                i++;
            }
        }
        if(listTrans.isEmpty()){
            isNullTrans = true;
            listTrans.add("문장 번역을 해보세요.");
        }else{
            listTrans.add("");
        }

        i = 0;
        if(mworker_act.getActTest().arrSentence != null) {
            isNullTest = false;
            String testInfo, testTitle, testPercent, testKinds;
            int plus;

            while (i < mworker_act.getActTest().arrSentence.size()) {
                testInfo = mworker_act.getActTest().arrSentence.get(i);
                plus = testInfo.indexOf('+');
                testTitle = testInfo.substring(0,plus); //시험제목
                testInfo = testInfo.substring(plus+1, testInfo.length()); //시험 평균 정답률

                plus = testInfo.indexOf('+');
                testPercent = testInfo.substring(0,plus); //시험제목
                testKinds = testInfo.substring(plus+1, testInfo.length()); //시험 평균 정답률

                listTest.add(testTitle);
                listTestPercent.add(testPercent);
                listTestKinds.add(testKinds);
                listTestNum.add(mworker_act.getActTest().arrSentenceNum.get(i));
                Log.d(TAG, "출제목록 =>" + mworker_act.getActTest().arrSentence.get(i));

                i++;
            }
        }
        if(listTest.isEmpty()){
            isNullTest = true;
            listTest.add("시험을 출제해보세요.");
            listTestPercent.add("");
            listTestKinds.add("");
        }

        cntNew = ( isNullNew ? 0 : listNew.size()-1 );
        cntRecord = ( isNullRecord ? 0 : listRecord.size()-1 );
        cntTrans = ( isNullTrans ? 0 : listTrans.size()-1 ) ;
        cntTest =( isNullTest ? 0 : listTest.size() ) ;
    }


    public void setTabColor() {
        for(int i=0;i< mTabHost.getTabWidget().getChildCount();i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.btn_border_fuzzy_peach); //선택되지 않은 탭
        }
        mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.fuzzy_peach)); //선택된 탭
    }
}