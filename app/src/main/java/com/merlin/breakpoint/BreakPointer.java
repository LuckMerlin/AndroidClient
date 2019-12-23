package com.merlin.breakpoint;

import java.util.List;

public interface BreakPointer {
    boolean addBreakpoint(BreakPoint breakpoint);
    boolean removeBreakpoint(Object breakpoint);
    List<BreakPoint> getBreakpoints();
}
