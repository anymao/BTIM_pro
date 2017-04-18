package top.anymore.btim_pro.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import top.anymore.btim_pro.R;

public class AboutActivity extends AppCompatActivity {
    private Button btnWeibo;//点击按钮跳转到指定页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btnWeibo = (Button) findViewById(R.id.btn_weibo);
        btnWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://m.weibo.cn/u/5297860046"));
                startActivity(intent);
            }
        });
    }
}
