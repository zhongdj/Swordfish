package net.madz.vehicle.biz;

public interface IConcreteTruck<ConcreteTruck> {

    // 1 工作中
    // 2 空闲中
    // 3 待修中
    // 4 修理中
    // 5 已售卖
    // 6 已分离
    
    // 0->2
    ConcreteTruck register();
    
    // 1->2
    void release();
    // 1->3
    // 2->3
    void applyMaintance();
    // 2->1
    void schedule();
    // 2->5
    // 3->5
    void sell();
    // 2->6
    // 3->6
    void detach();
    // 3->4
    void maintain();
    // 4->2
    void completeMaintance();
}
