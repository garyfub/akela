/*
 * Copyright 2012 Mozilla Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mozilla.pig.eval.date;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

import com.mozilla.util.DateUtil;

public class TimeDelta  extends EvalFunc<Long> {

    public static enum ERRORS { DateParseError };
    
    private SimpleDateFormat sdf;
    private long currentDay;
    private int deltaUnit;
    
    public TimeDelta(String day, String dateFormat, String deltaUnitStr) throws ParseException {
        sdf = new SimpleDateFormat(dateFormat);
        Date d = sdf.parse(day);
        currentDay = DateUtil.getTimeAtResolution(d.getTime(), Calendar.DATE);
        deltaUnit = Integer.parseInt(deltaUnitStr);
    }
    
    @Override
    public Long exec(Tuple input) throws IOException {
        if (input == null || input.size() == 0) {
            return null;
        }
        
        Long delta = null;
        try {
            Date d = sdf.parse((String)input.get(0));
            delta = DateUtil.getTimeDelta(d.getTime(), currentDay, deltaUnit); 
        } catch (ParseException e) {
            pigLogger.warn(this, "Date parsing error", ERRORS.DateParseError);
        }
        
        return delta;
    }

}
