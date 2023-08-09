/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package xyz.bluspring.forgecapabilities.capabilities;

import java.lang.reflect.ParameterizedType;

import org.objectweb.asm.Type;

/**
 * Inspired by {@link com.google.common.reflect.TypeToken TypeToken}, use a subclass to capture
 * generic types. Then uses CapabilityTokenSubclass a transformer
 * to convert that generic into a string returned by {@link #getType}
 * This allows us to know the generic type, without having a hard reference to the
 * class.
 *
 * Example usage:
 * <pre>{@code
 *    public static Capability<IDataHolder> DATA_HOLDER_CAPABILITY
 *    		= CapabilityManager.get(new CapabilityToken<>(){});
 * }</pre>
 *
 */
public abstract class CapabilityToken<T>
{
    protected final String getType()
    {
		// Gets the type name of the generic.
        var type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return Type.getInternalName((Class<T>) type.getActualTypeArguments()[0]);
    }

    @Override
    public String toString()
    {
        return "CapabilityToken[" + getType() + "]";
    }
}
