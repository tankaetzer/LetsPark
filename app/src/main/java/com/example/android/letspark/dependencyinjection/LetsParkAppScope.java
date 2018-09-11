package com.example.android.letspark.dependencyinjection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Helps in creating a singleton object. When this annotation is added with @Provides annotation
 * than it makes dagger to create an object only once and use same object in future.
 */
@Scope
@Retention(RetentionPolicy.CLASS)
public @interface LetsParkAppScope {
    // Intended to be blank.
}
