package com.example.android.letspark.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class hold the response from Distance Matrix API.
 */
public class DistanceMatrix {

    @SerializedName("status")
    public String status;

    @SerializedName("rows")
    public List<DistanceMatrixInfo> rows;

    public class DistanceMatrixInfo {
        @SerializedName("elements")
        public List<DistanceElement> elements;

        public class DistanceElement {
            @SerializedName("status")
            public String status;
            @SerializedName("duration")
            public ValueItem duration;
            @SerializedName("distance")
            public ValueItem distance;
        }

        public class ValueItem {
            @SerializedName("value")
            public long value;
            @SerializedName("text")
            public String text;
        }
    }
}
