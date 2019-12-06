package org.techtown.myapplication.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.myapplication.R;
// Layout1.xml 이 카드뷰 파일입니다.
// 이 카드뷰를 리사이클러뷰에 담기 위해,
// 데이터관리기능과, 각종 아이템의 뷰를 생성하는 VoiceAdapter.java 어뎁터 파일과
// 어댑터에 담는 각 아이템의 데이터를 담는 클래스를 Voice.java 아이템 데이터 파일로 만들었습니다.

// 이 리싸이클러뷰를 포함한 뷰그룹(프래그먼트)이 NotificationsFragment.java(소스파일), fragment_notifications.xml(디자인파일) 입니다.
// fragment_notifications.xml(디자인파일)에는, 리사이클러뷰(껍데기)를 넣어두었습니다.
public class NotificationsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }


    // 보통 MainActivity가 띄워지고 난 후에, 그 MainActivity클래스 내에서 RecyclerView를 참조함.
    // 액티비티가 onCreate()완료했을때 호출하는 프래그먼트의 onActivityCreated()를 재정의해서 그곳에 RecyclerView를 넣어 처리하면~
    // 거기서 하나 여기서 하나 똑같을 것임.

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        VoiceAdapter voiceAdapter = new VoiceAdapter();


        //////////// 리싸이클러뷰에 카드를 하나씩 추가합니다 (커스터마이징 가능) ////////////
        // 목소리 배열은 VoiceAdapter.java의 배열(voice_items)을 참조하여 "숫자"를 기입하여 주십시오.
        voiceAdapter.addItem(new Voice("해외",0));
        voiceAdapter.addItem(new Voice("국내",2));
        voiceAdapter.addItem(new Voice("국내",4));


        recyclerView.setAdapter(voiceAdapter);
    }
}