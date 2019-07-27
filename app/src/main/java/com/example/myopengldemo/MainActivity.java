package com.example.myopengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<Shape> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initData();
        mListView=findViewById(R.id.list);
        mListView.setAdapter(new MyAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,mArrayList.get(i).clazz);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mArrayList=new ArrayList<>();
        add("三角形",TriangleActivity.class);
        add("正三角形",RegularTriangleActivity.class);
        add("彩色正三角形",TriangleColorFullActivity.class);
        add("可旋转的彩色正三角形",RotateTriangleActivity.class);
        add("正方形",SquareActivity.class);
        add("圆形",OvalActvity.class);
    }

    private void add(String name, Class<?> clazz) {
        Shape shape=new Shape(name,clazz);
        mArrayList.add(shape);
    }

    public void btSquare(View view) {
        startActivity(new Intent(this,SquareActivity.class));
    }

    public void btTriangle(View view) {
        startActivity(new Intent(this,TriangleActivity.class));
    }

    public void btRegularTriangle(View view) {
        startActivity(new Intent(this,RegularTriangleActivity.class));
    }

    public void btTriangleColorFull(View view) {
        startActivity(new Intent(this,TriangleColorFullActivity.class));
    }

    public void btRotateTriangle(View view) {
        startActivity(new Intent(this,RotateTriangleActivity.class));
    }

    private class Shape {
        String name;
        Class<?> clazz;

        public Shape(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return mArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view==null){
                view=LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
                view.setTag(new ViewHolder(view));
            }
            ViewHolder holder=(ViewHolder)view.getTag();
            holder.setShape(mArrayList.get(i));
            return view;
        }

        private class ViewHolder{
            private TextView mName;
            private ViewHolder(View parent){
                mName=parent.findViewById(android.R.id.text1);
            }
            public void setShape(Shape shape){
                mName.setText(shape.name);
            }
        }
    }
}
