package com.onpuri;

import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by HYERIM on 2016-07-14.
 */
public class SharedPreferences implements android.content.SharedPreferences {
    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
