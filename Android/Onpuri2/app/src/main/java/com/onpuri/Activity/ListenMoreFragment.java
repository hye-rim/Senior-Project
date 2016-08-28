package com.onpuri.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.DividerItemDecoration;
import com.onpuri.Listener.EndlessRecyclerOnScrollListener;
import com.onpuri.Listener.HomeItemClickListener;
import com.onpuri.Adapter.ListenListAdapter;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.onpuri.R.drawable.divider_dark;


public class ListenMoreFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    private static final String TAG = "ListenMoreFragment";
    private static View view;

    ArrayList<String> list_listen;
    ArrayList<String> list_userid;
    ArrayList<String> list_day;
    ArrayList<String> list_reco;
    ArrayList<String> list_num;

    TextView item;
    String sentence = "";
    String sentence_num = "";
    TextToSpeech tts;

    private android.support.v7.widget.RecyclerView RecyclerView;
    private ListenListAdapter Adapter;
    protected RecyclerView.LayoutManager LayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_listen_more, container, false);
        } catch (InflateException e) {
        }

        list_listen = new ArrayList<String>();
        list_userid = new ArrayList<String>();
        list_day = new ArrayList<String>();
        list_reco = new ArrayList<String>();
        list_num = new ArrayList<String>();

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=getArguments().getString("sen_num");
            item.setText(sentence);
        }
        listen();

        ImageButton tts_sen = (ImageButton) view.findViewById(R.id.tts);
        tts_sen.setOnClickListener(this);
        ImageButton add_note = (ImageButton) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        ImageButton add_listen = (ImageButton) view.findViewById(R.id.add_listen);
        add_listen.setOnClickListener(this);

        tts = new TextToSpeech(getActivity(), this);

        RecyclerView = (RecyclerView) view.findViewById(R.id.recycler_listen);
        LayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(LayoutManager);
        Adapter = new ListenListAdapter(list_listen, RecyclerView);
        RecyclerView.setAdapter(Adapter);// Set CustomAdapter as the adapter for RecyclerView.
        RecyclerView.addOnItemTouchListener(
                new HomeItemClickListener(getActivity().getApplicationContext(), RecyclerView ,new HomeItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("선택한 듣기를 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        final FragmentManager fm = getActivity().getSupportFragmentManager();
                                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        fm.popBackStack();
                                        ft.commit();
                                        Toast.makeText(getActivity(), "삭제되었습니다(구현예정)", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dlg, int sumthin) {
                                        Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
                                    }

                                }).show();
                    }
                })
        );

        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), divider_dark);
        RecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        return view;
    }

    private void listen(){}

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final Bundle args = new Bundle();
        args.putString("sen", sentence);
        args.putString("sen_num", sentence_num);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.add_note:
                final CharSequence[] items = {"노트1", "노트2", "노트3"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("노트를 선택해 주세요(노트 연동은 구현 예정)")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                Toast.makeText(getActivity(), items[index] + "선택", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;
            case R.id.add_listen:
                final ListenAddFragment alf = new ListenAddFragment();
                alf.setArguments(args);
                ft.replace(R.id.root_home, alf);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.tts:
                tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                break;
        }
    }

    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }
}