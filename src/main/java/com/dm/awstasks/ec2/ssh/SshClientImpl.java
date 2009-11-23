package com.dm.awstasks.ec2.ssh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;

import com.dm.awstasks.ssh.JschRunner;
import com.dm.awstasks.ssh.ScpDownloadCommand;
import com.dm.awstasks.ssh.ScpUploadCommand;

public class SshClientImpl implements SshClient {

    protected static final Logger LOG = Logger.getLogger(SshClientImpl.class);

    protected final File _privateKey;
    protected final String _username;
    protected final List<String> _hostnames;

    public SshClientImpl(File privateKey, List<String> hostnames, String username) {
        _privateKey = privateKey;
        _hostnames = hostnames;
        _username = username;
    }

    @Override
    public void executeCommand(String command) {
        executeCommand(_hostnames, command);
    }

    @Override
    public void executeCommand(String command, int[] targetedInstances) {
        executeCommand(getHosts(targetedInstances), command);
    }

    private void executeCommand(List<String> hostnames, String command) {
        for (String host : hostnames) {
            LOG.info(String.format("executing command '%s' on '%s'", command, host));
            SSHExec sshExec = createSshExec(host);
            sshExec.setCommand(command);
            sshExec.execute();
        }
    }

    @Override
    public void executeCommandFile(File commandFile) {
        executeCommandFile(_hostnames, commandFile);
    }

    @Override
    public void executeCommandFile(File commandFile, int[] targetedInstances) {
        executeCommandFile(getHosts(targetedInstances), commandFile);
    }

    private void executeCommandFile(List<String> hostnames, File commandFile) {
        for (String host : hostnames) {
            LOG.info(String.format("executing command-file '%s' on '%s'", commandFile.getAbsolutePath(), host));
            SSHExec sshExec = createSshExec(host);
            sshExec.setCommandResource(commandFile.getAbsolutePath());
            sshExec.execute();
        }
    }

    private SSHExec createSshExec(String host) {
        SSHExec sshExec = new SSHExec();
        configureSshBase(sshExec, host);
        return sshExec;
    }

    protected void configureSshBase(org.apache.tools.ant.taskdefs.optional.ssh.SSHBase sshExec, String host) {
        // sshExec.setProject(new Project());
        sshExec.setUsername(_username);
        sshExec.setKeyfile(_privateKey.getAbsolutePath());
        sshExec.setTrust(true);
        sshExec.setHost(host);
        sshExec.setVerbose(true);
        sshExec.setFailonerror(true);
    }

    public void uploadFile(File localFile, String targetPath) throws IOException {
        uploadFile(_hostnames, localFile, targetPath);
    }

    @Override
    public void uploadFile(File localFile, String targetPath, int[] instanceIndex) throws IOException {
        List<String> hostnames = getHosts(instanceIndex);
        uploadFile(hostnames, localFile, targetPath);
    }

    private void uploadFile(List<String> hosts, File localFile, String targetPath) throws IOException {
        for (String host : hosts) {
            LOG.info(String.format("uploading file '%s' to '%s'", localFile.getAbsolutePath(), constructRemotePath(host, targetPath)));
            JschRunner jschRunner = createJschRunner(host);
            jschRunner.run(new ScpUploadCommand(localFile, targetPath));
        }
    }

    public void downloadFile(String remoteFile, File localPath, boolean recursiv) throws IOException {
        downloadFiles(_hostnames, remoteFile, localPath, recursiv);
    }

    @Override
    public void downloadFile(String remoteFile, File localPath, boolean recursiv, int[] instanceIndex) throws IOException {
        List<String> hosts = getHosts(instanceIndex);
        downloadFiles(hosts, remoteFile, localPath, recursiv);
    }

    private void downloadFiles(List<String> hostnames, String remoteFile, File localPath, boolean recursiv) throws IOException {
        for (String host : hostnames) {
            LOG.info(String.format("downloading file '%s' to '%s'", constructRemotePath(host, remoteFile), localPath.getAbsolutePath()));
            JschRunner jschRunner = createJschRunner(host);
            jschRunner.run(new ScpDownloadCommand(remoteFile, localPath, recursiv));
        }
    }

    private String constructRemotePath(String host, String filePath) {
        return _username + ":" + "@" + host + ":" + filePath;
    }

    protected JschRunner createJschRunner(String host) {
        JschRunner runner = new JschRunner(_username, host);
        runner.setKeyfile(_privateKey.getAbsolutePath());
        runner.setTrust(true);
        return runner;
    }

    protected List<String> getHosts(int[] instanceIndex) {
        List<String> hostnames = new ArrayList<String>(_hostnames.size());
        for (int i = 0; i < instanceIndex.length; i++) {
            hostnames.add(_hostnames.get(instanceIndex[i]));
        }
        return hostnames;
    }

}