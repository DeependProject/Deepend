/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.minecade.deepend.client.channels.impl;

import com.minecade.deepend.channels.Channel;
import com.minecade.deepend.channels.DeependChannel;
import com.minecade.deepend.connection.DeependConnection;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.request.StatusRequest;
import lombok.NonNull;

/**
 * Created 2/26/2016 for Deepend
 *
 * @author Citymonstret
 */
public class CheckData extends DeependChannel {

    public CheckData() {
        super(Channel.CHECK_DATA);
    }

    @Override
    public void act(@NonNull DeependConnection connection, DeependBuf buf) {
        DeependBuf in = connection.getBuf("in");
        String getID = in.getString();
        Logger.get().debug("Getting response for: " + getID);
        StatusRequest request = StatusRequest.getRequest(getID);
        if (request == null) {
            Logger.get().error("Got response for unregistered request, throwing!");
            return;
        }
        request.call(in.getInt());
    }
}
