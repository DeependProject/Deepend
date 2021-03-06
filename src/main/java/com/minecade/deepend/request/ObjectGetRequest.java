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

package com.minecade.deepend.request;

import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.object.DeependObject;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * This is a wrapper for GetRequest
 * that allows you to specify the object
 *
 * @author Citymonstret
 * @see com.minecade.deepend.request.GetRequest
 */
public class ObjectGetRequest extends GetRequest
{

    private DeependObject object;

    @Getter(AccessLevel.PROTECTED)
    private String key;

    /**
     * Constructor
     *
     * @param object        DeependObject to use for the request
     * @param dataRecipient Recipient that will handle incoming data
     */
    public ObjectGetRequest(String requestedKey, DeependObject object, DataRecipient dataRecipient)
    {
        super( dataRecipient );
        this.object = object;
        this.key = requestedKey;
    }

    @Override
    protected void buildRequest(DeependBuf buf)
    {
        buf.writeByte( this.object.getObjectType() );
        this.object.request( getKey(), buf );
    }
}
