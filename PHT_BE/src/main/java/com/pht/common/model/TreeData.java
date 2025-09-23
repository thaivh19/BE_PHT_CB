package com.pht.common.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
public abstract class TreeData<ID> {

    @Transient
    private Integer level;

    @Transient
    private boolean hasChild;

    @Transient
    private List<? extends TreeData<ID>> childs;

    public boolean isHasChild() {
        return childs != null && !childs.isEmpty();
    }

    public abstract Integer getLevel();

    public abstract ID getNodeId();

    public abstract ID getParentNodeId();

    public boolean isRoot() {
        return getParentNodeId() == null;
    }

    @SuppressWarnings("unchecked")
    public <T extends TreeData<ID>> void addChild(T child) {
        if (childs == null) {
            childs = new ArrayList<>();
        }

        ((List<T>) childs).add(child);
    }
}
