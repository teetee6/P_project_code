package org.techtown.myapplication.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.myapplication.R;

import java.util.ArrayList;
// 리싸이클러뷰의 어뎁터를 설정하는 곳입니다.(리싸이클러뷰를 어뎁터로의 연결은 메인페이지(NotificationsFragment.java)에서 합니다)
// 리싸이클러뷰 안에 스피너를 넣을 것이니, 스피너에 대한 어뎁터 설정도 이곳에서 합니다.(스피너에 대한 어뎁터는 여기서 미리 연결합니다)
// (메인페이지(NotificationsFragment.java)에선, 완성된 리싸이클러뷰로 호출만 하기 위함)

public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.ViewHolder>{
    ArrayList<Voice> items = new ArrayList<Voice>();
    Spinner spinner;
    ArrayAdapter<String> spinner_adapter;

    //////////// 목소리 배열을 설정합니다 (커스터마이징 가능) ////////////
    String[] voice_items = { "mike", "angel", "crow", "john", "뽀로로" };


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout1, parent, false);

        spinner = itemView.findViewById(R.id.voiceSpinner);
        spinner_adapter = new ArrayAdapter<String>(
                parent.getContext() , android.R.layout.simple_spinner_dropdown_item, voice_items);
        spinner.setAdapter(spinner_adapter);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int recycle_position) {  // 리싸이클러뷰가 재사용되면
        final Voice item = items.get(recycle_position);   // 재사용된 Voice객체의 위치(item)을 찾아

        // 그 위치에 해당하는 spinner가 반응하는 이벤트.
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spin_position, long id) {

                // onItemSelected()메서드가 실행되면 (즉, 목소리가 "뽀로로"->"하리수"로 스피너의 아이템이 선택되었다면)
                // item 데이터객체 내부의 Character변수 값도 갱신해줘야 합니다.
                // 배열 내의 탐색을 위해, [자료구조]의 간단한 [배열 요소 위치검색]함수(search_location())을 만들었습니다.
                int where = search_location(voice_items[spin_position]);   // ("뽀로로") => return 4;
                item.setCharacter(where);   // item 객체 내의 Character값(前)을, 사용자가 선택한 값(spin_position)의 배열 위치값으로 설정합니다.

                //////////// 여기서 스피너 처리이벤트를 실행합니다 (커스터마이징 가능) ////////////
                Toast.makeText( view.getContext() ,
                        "국가는: [" + item.getNation() + "],,,,,  " +
                                "목소리는: [" + voice_items[spin_position] + "],,,,,   " +
                                "데이터 객체 내에 실제 저장된 목소리는: [" + item.getCharacter() + "]" ,
                                Toast.LENGTH_SHORT).show();
                //////////// 스피너 처리이벤트 커스터마이징 끝 영역 ////////////

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Voice item) {
        items.add(item);
    }

    public void setItems(ArrayList<Voice> items) {
        this.items = items;
    }

    public Voice getItem(int position) {
        return items.get(position);
    }
//
    public void setItem(int position, Voice item) {
        items.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Spinner spinner;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textNation);
            spinner = itemView.findViewById(R.id.voiceSpinner);
        }

        public void setItem(Voice item) {
            textView.setText(item.getNation());
            spinner.setSelection(item.getCharacter());
        }
    }

    // 단순 배열 내 요소 위치 반환함수
    public int search_location(String search_item) {

        for (int i=0; i < voice_items.length; i++) {
            if(voice_items[i] == search_item) {
                return i;
            }
        }

        return -1;      // 스피너 내에서 설정된 값을 탐색하는거라, 무조건 위 for()문에서 결과값이 나오니, 절대 실행될 일 없음.
    }
}
