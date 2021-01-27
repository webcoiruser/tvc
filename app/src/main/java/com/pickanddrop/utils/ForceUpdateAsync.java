package com.pickanddrop.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.pickanddrop.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


/**
 * Created by swayamagrawal on 28/09/18.
 */

public class ForceUpdateAsync extends AsyncTask<Void, String, String> {

    private String latestVersion;
    private String currentVersion;
    private Context context;
    public ForceUpdateAsync(String currentVersion, Context context){
        this.currentVersion = currentVersion;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String newVersion = null;

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                            System.out.println("veeeeverrr-->"+newVersion);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;

    }


    @Override

    protected void onPostExecute(String onlineVersion) {

        super.onPostExecute(onlineVersion);
        Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
            try {
                if(onlineVersion.equalsIgnoreCase(currentVersion)){
                    System.out.println("skdjfhks");

                }else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Update");
                        alertDialog.setIcon(R.drawable.loginlogo);
                        alertDialog.setMessage("New Update is available");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                                }
                            }
                        });
                        alertDialog.show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
        }


}