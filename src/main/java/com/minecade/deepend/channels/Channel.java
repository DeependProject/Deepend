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

package com.minecade.deepend.channels;

import com.minecade.deepend.object.ByteProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * These are the channels that
 * are supported by the default protocol
 */
public enum Channel implements ByteProvider {

    /**
     * Authentication channel
     */
    AUTHENTICATE((byte) 0x03),

    /**
     * Used to check listen for
     * quality
     */
    ECHO((byte) 0x04),

    /**
     * Add data to the server
     */
    ADD_DATA((byte) 0x05),

    /**
     * Get data from the server
     */
    GET_DATA((byte) 0x06),

    /**
     * Update data on the server
     */
    UPDATE_DATA((byte) 0x07),

    /**
     * Remove data from the server
     */
    REMOVE_DATA((byte) 0x08),

    /**
     * Subscribe to server updates
     */
    SUBSCRIBE((byte) 0x09),

    /**
     * Unknown = Error, sort of
     */
    UNKNOWN((byte) -1);

    private final byte id;

    Channel(final byte id) {
        this.id = id;
    }

    @Override
    public byte getByte() {
        return this.id;
    }

    private static Map<Byte, Channel> cache = new HashMap<>();

    static {
        for (Channel channel : values()) {
            cache.put(channel.getByte(), channel);
        }
    }

    /**
     * Get the channel based on its ID
     * @param id ID
     * @return Channel | UNKNOWN
     */
    public static Channel getChannel(byte id) {
        if (!cache.containsKey(id)) {
            return UNKNOWN;
        }
        return cache.get(id);
    }
}