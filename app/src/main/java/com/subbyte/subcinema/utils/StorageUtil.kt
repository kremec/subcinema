package com.subbyte.subcinema.utils

import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.context.CIFSContextWrapper
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import java.util.Properties

object StorageUtil {
    fun getSmbFile(path: String): SmbFile {
        jcifs.Config.registerSmbURLHandler()
        val config = PropertyConfiguration(
            Properties().apply {
                setProperty("jcifs.smb.client.enableSMB2", "true")
            }
        )
        val smbAuth = NtlmPasswordAuthenticator(
            SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbDomain.key, SettingsUtil.EntryBrowser_SmbDomain.defaultValue as String),
            SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbUsername.key, SettingsUtil.EntryBrowser_SmbUsername.defaultValue as String),
            SettingsUtil.getData(SettingsUtil.EntryBrowser_SmbPassword.key, SettingsUtil.EntryBrowser_SmbPassword.defaultValue as String)
        )
        val smbFile = SmbFile(
            path,
            CIFSContextWrapper(
                BaseContext(config).withCredentials(smbAuth)
            )
        )
        return smbFile
    }
}