/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

package com.amazon.corretto.demo;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;

import java.lang.String;
import java.lang.System;
import java.lang.Thread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeapMemoryAfterGCDemo {

    private static Set<MemoryPoolMXBean> heapMemoryPools = null;

    public static void main(String[] args) {
        HashMap<String, String> some_memory = new HashMap<String, String>();
        long iter_count = 0;
        int new_entry_count = 5000;
        while (true) {
            try {
                // Allocate some memory
                String some_string = new String();
                for (int i = new_entry_count; i > 0; --i) {
                    some_string = some_string + new_entry_count;
                    String index_string = Long.valueOf(iter_count * 10000000 + i).toString();
                    some_memory.put(index_string, new String(some_string));
                }
                ++iter_count;

                // Collect (promote to old gen)
                System.gc();

                // Get heap usage
                getAfterGCHeapMemoryUsage();

                // Wait a second and do it again
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private static void getAfterGCHeapMemoryUsage() throws Exception {
        if (heapMemoryPools == null) {
            heapMemoryPools = getMemoryPools(ManagementFactory.getMemoryPoolMXBeans());
        }
        emitUsageForPools(heapMemoryPools);
    }

    private static HashSet<MemoryPoolMXBean> getMemoryPools(List<MemoryPoolMXBean> memoryPoolMXBeans) throws Exception {
        System.out.println("getMemoryPools");
        HashSet<MemoryPoolMXBean> memoryPools = new HashSet<MemoryPoolMXBean>();
        for (MemoryPoolMXBean pool : memoryPoolMXBeans) {
            System.out.println("Pool " + pool.getName() + " Type " + pool.getType());
            if (pool.getType().toString().contains("Non-heap") || pool.getName().contains("Eden")) {
                // Ignore non-HEAP memory pools
                // Ignore Eden, since it's just an allocation buffer
                continue;
            }
            System.out.println("Add Pool " + pool.getName());
            memoryPools.add(pool);
        }
        return memoryPools;
    }

    private static void emitUsageForPools(Set<MemoryPoolMXBean> memoryPools) throws Exception {
        long usedKb = 0, totalKb = 0;
        for (MemoryPoolMXBean pool : memoryPools) {
            MemoryUsage usage = pool.getCollectionUsage();

            long used = usage.getUsed();
            usedKb += used / 1024;

            long max = usage.getMax();
            // max can be undefined (-1) http://docs.oracle.com/javase/7/docs/api/java/lang/management/MemoryUsage.html
            totalKb += max > 0 ? max / 1024 : 0;
        }
        System.out.print(System.currentTimeMillis() + ": HeapMemoryAfterGC: " + usedKb + "K / " + totalKb + 'K');
        if (totalKb > 0) {
            System.out.print(", Usage = " + ((double)usedKb / (double)totalKb) * 100 + '%');
        }
        System.out.println();
    }
}
