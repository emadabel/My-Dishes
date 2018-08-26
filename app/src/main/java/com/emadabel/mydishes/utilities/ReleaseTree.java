package com.emadabel.mydishes.utilities;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class ReleaseTree extends Timber.Tree {

    @Override
    protected boolean isLoggable(@Nullable String tag, int priority) {
        return priority >= Log.INFO;
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        // TODO: log all messages

        if (t != null) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                // TODO: log throwable
            }
        }

    }
}
