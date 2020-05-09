package com.example.app.factories;

public abstract class UrlFactory {
   public static String getUrl(String domain, String[] label, String[] value) {
        StringBuilder url = new StringBuilder(domain);
        url.append('?');
        for(int i = 0; i < label.length; i++){
            if(i != 0){
                url.append('&');
            }
            url.append(label[i]);
            url.append('=');
            url.append(value[i]);
        }
        return url.toString();
   }
}
