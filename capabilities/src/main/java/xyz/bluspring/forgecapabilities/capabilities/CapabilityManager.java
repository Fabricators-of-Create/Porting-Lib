package xyz.bluspring.forgecapabilities.capabilities;

import java.util.ArrayList;
import java.util.IdentityHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public enum CapabilityManager
{
    INSTANCE;
    static final Logger LOGGER = LogManager.getLogger();


    public static <T> Capability<T> get(CapabilityToken<T> type)
    {
        return INSTANCE.get(type.getType(), false);
    }

    @SuppressWarnings("unchecked")
    <T> Capability<T> get(String realName, boolean registering)
    {
        Capability<T> cap;

        synchronized (providers)
        {
            realName = realName.intern();
            cap = (Capability<T>)providers.computeIfAbsent(realName, Capability::new);
        }


        if (registering)
        {
            synchronized (cap)
            {
                if (cap.isRegistered())
                {
                    LOGGER.error("Cannot register capability implementation multiple times : {}", realName);
                    throw new IllegalArgumentException("Cannot register a capability implementation multiple times : "+ realName);
                }
                else
                {
                    cap.onRegister();
                }
            }
        }

        return cap;
    }

    // INTERNAL
    private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap<>();
	public void injectCapabilities() {
		var capabilities = new ArrayList<Class<?>>();
		RegisterCapabilitiesCallback.EVENT.invoker().onRegisterCapability(capabilities);

		for (Class<?> type : capabilities) {
			this.get(Type.getInternalName(type), true);
		}
	}
}
