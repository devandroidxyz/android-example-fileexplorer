//http://forum.codecall.net/topic/79689-creating-a-file-browser-in-android/
package com.example.fileexplorer.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fileexplorer.R;
import com.example.fileexplorer.contract.OnBackKeyPressedListener;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//@@@@
        //        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.main_activity);

        //パーミッション許可確認
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,REQUEST_CODE);

    }

    @Override
    public void onBackPressed() {
        boolean isRoot = false;
        //fragment側でBackキーを受け取るため
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("handlingBackPressed");
        if (fragment instanceof OnBackKeyPressedListener) {
            isRoot = ((OnBackKeyPressedListener) fragment).onBackPressed();
        }
        //ディレクトリがルートの場合のみ制御実行しアプリ終了
        if(isRoot) super.onBackPressed();
    }

    // パーミッションの状態を確認して、各処理に飛ばす
    // @@@@ Activityに配置すべきかGoogleサンプルを確認しよう
    private void checkPermission(final String permission, final int request_code){
        // ・現在地取得のパーミッションの許可確認
        // 許可されていない
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可をリクエスト
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        }
        // パーミッションが許可されている
        else {
            Toast.makeText(this, "権限が許可されています", Toast.LENGTH_SHORT).show();
            // 以下通常処理等に飛ばす・・・
            getFragment();
        }
    }

    // requestPermissionsのコールバック
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "パーミッション追加しました", Toast.LENGTH_SHORT).show();
                // 以下通常処理等に飛ばす・・・
                getFragment();
            } else {
                Toast.makeText(this, "パーミッション追加できませんでした", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFragment(){

        UIView list = new UIView();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.file_list, list)
                .commitNow();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.file_list, list, "handlingBackPressed")
                .addToBackStack(null).commit();
    }
}
