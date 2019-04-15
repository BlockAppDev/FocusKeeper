package com.focuskeeper;

import java.util.List;

public interface BlockController<T> {
    public List<T> getBlockItems();

    public void addBlockItem(T blockItem);

    public void addBlockItems(List<T> blockItems);

    public void enable();

    public void disable();
}