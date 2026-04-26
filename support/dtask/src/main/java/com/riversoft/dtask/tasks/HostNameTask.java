package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Hostname task
 * <p/>
 * Used to input and verify a hostname. The user can also be presented with
 * suggestions on what to use from the local machine's Network Interface Cards
 * <p/>
 * When a hostname has been entered, a lookup is performed to verify that the
 * host entered exists and can be found. The task will not complete unless a
 * proper hostname or IP adress has been supplied.
 */
public class HostNameTask extends BaseRiverTask {

    static final String PROMPT_USE_SAVED = "%1$s" + NEW_LINE + "Do you want to use previous setting of: [%2$s] [y]/n?";

    /**
     * Attribute prompt
     * <p/>
     * Displayed to user when asked to input hostname
     */
    private String prompt = "Hostname:";

    /**
     * Attribute addProperty
     * <p/>
     * The name of the variable which to save the resulting hostname to.
     */
    private String addProperty;

    /**
     * Attribute suggest
     * <p/>
     * If set to true, indicates that menu selection of NICs addresses and
     * hostnames will be presented to user
     */
    private boolean suggest = false;

    /**
     * Attribute allowInput
     * <p/>
     * If set to false, does not allow the user to select hostname different
     * from the hostnames presented in the suggest menu. Only valid in conjunction
     * with suggest
     */
    private boolean allowInput = true;

    /**
     * Attribute prefername
     * <p/>
     * If set, always use the hostname as value if the lookup is ok
     * even if getCanonicalHostname returns differently
     */
    private boolean preferName = true;

    /**
     * Attribute useLocal
     * <p/>
     * Indicates if local addresses (Loopback device, localhost) should be
     * displayed in suggest menu
     * <p/>
     * Default: false
     */
    private boolean useLocal = false;

    /**
     * Attribute confirm
     * <p/>
     * Indicates if the host should be confirmed after being selected.
     * <p/>
     * Default: false
     */
    private boolean confirm = false;

    /** */
    private String propertyValue;

    /**
     * @param useLocal
     * @see #useLocal
     */
    public void setUseLocal(String useLocal) {
        this.useLocal = Boolean.parseBoolean(useLocal);
    }

    /**
     * @param confirm
     * @see #confirm
     */
    public void setConfirm(String confirm) {
        this.confirm = Boolean.parseBoolean(confirm);
    }

    /**
     * @param addProperty
     * @see #addProperty
     */
    public void setAddproperty(String addProperty) {
        this.addProperty = addProperty;
    }

    /**
     * @param allowInput
     * @see #allowInput
     */
    public void setAllowInput(String allowInput) {
        this.allowInput = Boolean.parseBoolean(allowInput);
    }

    /**
     * @param suggest
     * @see #suggest
     */
    public void setSuggest(String suggest) {
        this.suggest = Boolean.parseBoolean(suggest);
    }

    /**
     * @param prompt
     * @see #prompt
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * @param preferName
     * @see #preferName
     */
    public void setPreferName(String preferName) {
        this.preferName = Boolean.getBoolean(preferName);
    }

    /**
     * @see com.BaseRiverTask.dtasks.tasks.BaseDruttTask#doExecute()
     */
    @Override
    protected void doExecute() throws BuildException {
        propertyValue = getProperty(addProperty);

        boolean useSaved = isUnattended && propertyValue != null;

        // Previous value was read from install configuration
        if (!isUnattended && propertyValue != null) {

            InputHandler inputHandler = this.getProject().getInputHandler();

            InputRequest ir = new InputRequest(String.format(PROMPT_USE_SAVED, prompt, propertyValue));

            boolean inputAccepted = false;
            while (!inputAccepted) {
                inputHandler.handleInput(ir);
                String inP = ir.getInput();

                if (EMPTY.equals(inP) || inP.equalsIgnoreCase("y")) {
                    useSaved = true;
                    inputAccepted = true;
                } else if (inP.equalsIgnoreCase("n")) {
                    inputAccepted = true;
                }
            }
        }

        boolean confirmed = useSaved;
        boolean singleChoice = false;

        while (!confirmed) {
            // reset propertyValue
            propertyValue = null;

            if (suggest) {
                // suggest returns true if only one valid choice
                singleChoice = suggest();
            }

            // Get input if suggest() did not return with host selected ("Other" choice made)
            if (propertyValue == null) {
                boolean lookupOK = false;

                while (!lookupOK) {
                    InputSelectTask it = new InputSelectTask();
                    it.setProject(getProject());
                    it.setOwningTarget(getOwningTarget());
                    it.setPrompt(suggest ? ">> " : prompt);
                    it.setSave("false");
                    it.doExecute();

                    String hostName = it.getValue();

                    if (hostName.matches("[0-9\\.\\s]+")
                            && !hostName
                            .matches("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b")) {
                        getProject().log("Malformed IP address. Check your input and try again", DRUTT_LOG_LVL);
                    } else {
                        try {
                            InetAddress address = InetAddress.getByName(hostName);
                            propertyValue = preferName ? hostName : address.getCanonicalHostName();
                            getProject().log(hostName + " resolved to " + address.getHostAddress(), DRUTT_LOG_LVL);
                            lookupOK = true;
                        } catch (UnknownHostException e) {
                            getProject().log("Host not found. Check your input and try again", DRUTT_LOG_LVL);
                        }
                    }
                }
            } else if (!suggest || singleChoice) {
                // To give context to user
                getProject().log(prompt, DRUTT_LOG_LVL);
            }

            if (confirm && !confirmed && !singleChoice) {
                ConfirmTask ct = new ConfirmTask();
                ct.setProject(getProject());
                ct.setOwningTarget(getOwningTarget());
                ct.setValue(propertyValue);
                ct.execute();
                confirmed = ct.getResult();
            } else {
                getProject().log("Using " + propertyValue, DRUTT_LOG_LVL);
                confirmed = true;
            }
        }

        setProjectProperty(addProperty, propertyValue);

        if (!useSaved) {
            // Only write properties if neccessary
            setProperty(addProperty, propertyValue);
            saveProperties();
        }
    }

    /**
     * Generates a menu of IPs/hostnames to choose from
     *
     * @return true if only one choice was available from suggest() in which case no
     * confirmation needs to be done for this choice
     */
    private boolean suggest() {
        StringBuffer sb = new StringBuffer();
        List<InetAddress> addrList = new ArrayList<InetAddress>();
        List<String> addrChoiceStrings = new ArrayList<String>();

        String headerString = "      IP   -   Resolved IP  ( Visibility )";
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();

            int cIx = 0;
            String mItemFormat = "%1$s - %2$s (%3$s)";

            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();

                Enumeration<InetAddress> adds = nif.getInetAddresses();
                while (adds.hasMoreElements()) {
                    InetAddress addr = adds.nextElement();

                    String IP = addr.getHostAddress();

                    String state;
                    if (addr.isLinkLocalAddress() || addr.isLoopbackAddress()) {
                        if (!useLocal) {
                            continue;
                        }
                        state = "Local";
                    } else if (addr.isSiteLocalAddress()) {
                        state = "Intranet";
                    } else {
                        state = "External";
                    }

                    String c = String.format(mItemFormat, IP, IP, state);

                    if (cIx != 0) {
                        sb.append(", ");
                    }
                    if (!sb.toString().contains(c)) {
                        sb.append(c);
                    }
                    addrList.add(addr);
                    addrChoiceStrings.add(c);
                    cIx++;
                }
            }
        } catch (SocketException e) {
            //
            getProject().log("Couldn't get network interfaces", DRUTT_LOG_LVL);
            throw new BuildException(getOwningTarget() + " : " + getTaskName() + " - Couldn't get network interfaces");
        }

        // If allowInput is false, the user HAS to choose from the given
        // addresses.
        if (allowInput) {
            // Else - choose to use different hostname
            sb.append(", Other(Please Input FQDN instead of hostname)");
        } else if (addrList.size() == 1) {
            // Only one choice available. No need to present choices to user
            propertyValue = addrList.get(0).getCanonicalHostName();
            return true;
        }

        MenuSelectTask st = new MenuSelectTask();
        st.setProject(getProject());
        st.setOwningTarget(getOwningTarget());
        // Don't save this result
        st.setSave("false");
        st.setChoices(sb.toString());
        st.setPrompt(prompt + NEW_LINE + "You can choose to use any of these addresses:" + NEW_LINE + NEW_LINE
                + headerString);
        st.doExecute();

        String choice = st.getChoice();
        if (!choice.equals("Other(Please Input FQDN instead of hostname)")) {
            //
            int choiceIx = addrChoiceStrings.indexOf(choice);
            propertyValue = preferName ? addrList.get(choiceIx).getHostAddress() : addrList.get(choiceIx)
                    .getHostAddress();

            int count = 0;
            for (InetAddress address : addrList) {
                if (address.getCanonicalHostName().equals(propertyValue)) {
                    count++;
                }
            }

            // Use IP-adress instead when ambigous hostname
            if (count > 1) {
                log("Ambiguous hostname, using IP instead", DRUTT_LOG_LVL);
                propertyValue = addrList.get(choiceIx).getHostAddress();
            }
        } else {
            propertyValue = null;
        }
        return false;
    }
}
