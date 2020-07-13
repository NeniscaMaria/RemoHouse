package org.circuitdoctor.core.service;

import org.circuitdoctor.core.model.ActionLogGSM;
import org.circuitdoctor.core.model.GSMController;
import org.circuitdoctor.core.model.User;

import java.util.List;
import java.util.Set;

public interface ActionLogGSMService {
    ActionLogGSM addActionLogGSM(ActionLogGSM actionLogGSM);
    List<ActionLogGSM> findAllActions(Long userID);
    void deleteActionsWithUser(User user);
    void deleteActionsWithGSMController(GSMController gsmController);
    List<ActionLogGSM> findAllActionsBeetwenDates(Long userId,String startDate,String endDate);
    List<ActionLogGSM> findAllActionsFromGSMsBeetwenDates(Long userId,List<Long> gsmIds,String startDate,String endDate);

}
