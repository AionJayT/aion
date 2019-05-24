package org.aion.vm;

import org.aion.avm.core.AvmConfiguration;
import org.aion.avm.core.AvmImpl;
import org.aion.avm.core.CommonAvmFactory;
import org.aion.interfaces.tx.Transaction;
import org.aion.vm.api.interfaces.KernelInterface;
import org.aion.vm.api.interfaces.SimpleFuture;
import org.aion.vm.api.interfaces.TransactionResult;

/**
 * A thread-safe access-point to the Aion Virtual Machine.
 */
public final class AionVirtualMachine {
    private final AvmImpl avm;

    private AionVirtualMachine(AvmImpl avm) {
        if (avm == null) {
            throw new IllegalStateException("Cannot initialize AionVirtualMachine with a null avm!");
        }
        this.avm = avm;
    }

    /**
     * Constructs a new Avm instance and starts it up.
     *
     * @return A new AVM.
     */
    public static AionVirtualMachine createAndInitializeNewAvm() {
        return new AionVirtualMachine(CommonAvmFactory.buildAvmInstanceForConfiguration(new AionCapabilities(), new AvmConfiguration()));
    }

    /**
     * Executes the given transactions.
     *
     * This method is thread-safe.
     *
     * @param kernelInterface The interface into the kernel.
     * @param transactions The transactions to execute.
     * @return The future results.
     */
    public synchronized SimpleFuture<TransactionResult>[] run(KernelInterface kernelInterface, Transaction[] transactions) {
        return this.avm.run(kernelInterface, transactions);
    }

    /**
     * Shuts down the AVM. This object can no longer be used once this method is called.
     *
     * This method is thread-safe.
     */
    public synchronized void shutdown() {
        this.avm.shutdown();
    }
}
