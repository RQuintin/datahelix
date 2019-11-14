/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scottlogic.deg.common.util.defaults;

import com.scottlogic.deg.common.profile.Granularity;

import java.time.LocalTime;

import static com.scottlogic.deg.common.util.Defaults.*;

public class TimeDefaults implements LinearDefaults {

    private static TimeDefaults singleton;

    private TimeDefaults() {
    }

    public static synchronized TimeDefaults get() {
        if (singleton == null)
            singleton = new TimeDefaults();
        return singleton;
    }

    @Override
    public LocalTime min() {
        return TIME_MIN;
    }

    @Override
    public LocalTime max() {
        return TIME_MAX;
    }

    @Override
    public Granularity<LocalTime> granularity() {
        return DEFAULT_TIME_GRANULARITY;
    }

}