package com.codestream.vcs

import com.codestream.codeStream
import com.codestream.protocols.webview.ReviewNotifications
import com.codestream.settings.ApplicationSettingsService
import com.codestream.settingsService
import com.codestream.webViewService
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.VcsCheckinHandlerFactory
import com.intellij.openapi.vcs.ui.RefreshableOnComponent
import git4idea.GitVcs
import org.eclipse.lsp4j.Range
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class CodeStreamCheckinHandlerFactory : VcsCheckinHandlerFactory(GitVcs.getKey()) {
    override fun createVcsHandler(panel: CheckinProjectPanel?): CheckinHandler {
        return object : CheckinHandler() {
            val applicationSettings = ServiceManager.getService(ApplicationSettingsService::class.java)
            override fun getAfterCheckinConfigurationPanel(parentDisposable: Disposable?): RefreshableOnComponent {
                val checkbox = JCheckBox("Request feedback", true)
                val layout = BorderLayout()
                val afterCheckinPanel = JPanel(layout)
                afterCheckinPanel.add(checkbox, "Center")
                return object : RefreshableOnComponent {
                    override fun getComponent(): JComponent {
                        return afterCheckinPanel
                    }

                    override fun refresh() {
                        checkbox.isSelected = applicationSettings.state.createReviewOnCommit
                    }

                    override fun saveState() {
                        applicationSettings.state.createReviewOnCommit = checkbox.isSelected
                    }

                    override fun restoreState() {
                        refresh()
                    }
                }
            }

            override fun checkinSuccessful() {
                val project = panel?.project ?: return
                if (!applicationSettings.state.createReviewOnCommit) return

                ApplicationManager.getApplication().invokeLater {
                    project.codeStream?.show {
                        project.webViewService?.postNotification(
                            ReviewNotifications.New(
                                null,
                                Range(),
                                "JB Commit Dialog",
                                true
                            )
                        )
                    }
                }
            }
        }
    }
}
