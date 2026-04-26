package com.riversoft.wx.annotation;

import com.riversoft.core.BeanFactory;
import com.riversoft.platform.translate.WxCommandSupportType;
import com.riversoft.wx.mp.command.MpCommand;
import com.riversoft.wx.qy.command.QyCommand;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @borball on 4/15/2016.
 */
public class WxAnnotatedCommandsHolder {

    private Logger logger = LoggerFactory.getLogger(WxAnnotatedCommandsHolder.class);

    private Map<WxCommandSupportType, Set<CommandInfo>> mpAnnotatedCommands = new HashMap<>();
    private Map<WxCommandSupportType, Set<CommandInfo>> qyAnnotatedCommands = new HashMap<>();

    private Map<String, MpCommand> mpAnnotatedCommandsInstances = new HashMap<>();
    private Map<String, QyCommand> qyAnnotatedCommandsInstances = new HashMap<>();

    /**
     * @param scanPackage the scanPackage to set
     */
    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    private String scanPackage = "com.riversoft";

    public static WxAnnotatedCommandsHolder getInstance(){
        return (WxAnnotatedCommandsHolder)BeanFactory.getInstance().getBean("wxAnnotatedCommandsHolder");
    }

    public void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(WxAnnotatedCommand.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(scanPackage)) {
            String clazzName = bd.getBeanClassName();
            Class<?> clazz;
            try {
                clazz = Class.forName(clazzName);
                if(MpCommand.class.isAssignableFrom(clazz)) {
                    WxAnnotatedCommand annotation = clazz.getAnnotation(WxAnnotatedCommand.class);
                    WxCommandSupportType[] types = annotation.types();
                    for(WxCommandSupportType supportType : types) {
                        if(!mpAnnotatedCommands.containsKey(supportType)) {
                            Set<CommandInfo> commands = new HashSet<>();
                            mpAnnotatedCommands.put(supportType, commands);
                        }
                        CommandInfo commandInfo = new CommandInfo(annotation.name(), annotation.desc(), StringUtils.join(annotation.types(), ","), clazzName);
                        mpAnnotatedCommands.get(supportType).add(commandInfo);
                    }

                    mpAnnotatedCommandsInstances.put(clazzName, (MpCommand)clazz.newInstance());
                }

                if(QyCommand.class.isAssignableFrom(clazz)) {
                    WxAnnotatedCommand annotation = clazz.getAnnotation(WxAnnotatedCommand.class);
                    WxCommandSupportType[] types = annotation.types();
                    for(WxCommandSupportType supportType : types) {
                        if(!qyAnnotatedCommands.containsKey(supportType)) {
                            Set<CommandInfo> commands = new HashSet<>();
                            qyAnnotatedCommands.put(supportType, commands);
                        }
                        CommandInfo commandInfo = new CommandInfo(annotation.name(), annotation.desc(), StringUtils.join(annotation.types(), ","), clazzName);
                        qyAnnotatedCommands.get(supportType).add(commandInfo);
                    }

                    qyAnnotatedCommandsInstances.put(clazzName, (QyCommand)clazz.newInstance());
                }
            } catch (Exception e) {
                logger.warn("WxAnnotatedCommandsHolder init failed:" + e.getMessage());
            }
        }

        for (WxCommandSupportType key : mpAnnotatedCommands.keySet()) {
            Set<CommandInfo> commands = mpAnnotatedCommands.get(key);
            for(CommandInfo command : commands) {
                logger.info("[公众号系统处理器: " + key.getShowName() + "] -> " + command.getCommandKey());
            }
        }
        for (WxCommandSupportType key : qyAnnotatedCommands.keySet()) {
            Set<CommandInfo> commands = qyAnnotatedCommands.get(key);
            for(CommandInfo command : commands) {
                logger.info("[企业号系统处理器: " + key.getShowName() + "] -> " + command.getCommandKey());
            }
        }
    }

    public Map<WxCommandSupportType, Set<CommandInfo>> getQyAnnotatedCommands() {
        return qyAnnotatedCommands;
    }

    public Map<WxCommandSupportType, Set<CommandInfo>> getMpAnnotatedCommands() {
        return mpAnnotatedCommands;
    }

    public MpCommand getMpCommandInstanceByClassName(String className) {
        return mpAnnotatedCommandsInstances.get(className);
    }

    public QyCommand getQyCommandInstanceByClassName(String className) {
        return qyAnnotatedCommandsInstances.get(className);
    }

    public static class CommandInfo {

        private String busiName;
        private String desc;
        private String supports;
        private String commandKey;

        public CommandInfo(String busiName, String desc, String supports, String commandKey) {
            this.busiName = busiName;
            this.desc = desc;
            this.supports = supports;
            this.commandKey = commandKey;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getSupports() {
            return supports;
        }

        public void setSupports(String supports) {
            this.supports = supports;
        }

        public String getBusiName() {
            return busiName;
        }

        public void setBusiName(String busiName) {
            this.busiName = busiName;
        }

        public String getCommandKey() {
            return commandKey;
        }

        public void setCommandKey(String commandKey) {
            this.commandKey = commandKey;
        }
    }
}
