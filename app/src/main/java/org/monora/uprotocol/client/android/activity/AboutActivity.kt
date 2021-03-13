/*
 * Copyright (C) 2019 Veli Tasalı
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.monora.uprotocol.client.android.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import org.monora.uprotocol.client.android.BuildConfig
import org.monora.uprotocol.client.android.R
import org.monora.uprotocol.client.android.app.Activity
import org.monora.uprotocol.client.android.config.AppConfig
import org.monora.uprotocol.client.android.util.Activities
import org.monora.uprotocol.client.android.util.Updates

class AboutActivity : Activity() {
    private val googlePlayFlavor = BuildConfig.FLAVOR == "googlePlay"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<View>(R.id.activity_about_home_button).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(AppConfig.URI_ORG_HOME)))
        }
        findViewById<View>(R.id.activity_about_github_button).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(AppConfig.URI_REPO_APP)))
        }
        findViewById<View>(R.id.activity_about_localize_button).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(AppConfig.URI_TRANSLATE)))
        }
        findViewById<View>(R.id.activity_about_telegram_button).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(AppConfig.URI_TELEGRAM_CHANNEL)))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actions_about, menu)
        menu.findItem(R.id.actions_about_check_for_updates).isVisible = !googlePlayFlavor

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (!googlePlayFlavor && Updates.hasNewVersion(applicationContext)) {
            menu?.findItem(R.id.actions_about_check_for_updates)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actions_about_feedback -> Activities.startFeedbackActivity(this)
            R.id.actions_about_changelog -> {
                startActivity(Intent(this, ChangelogActivity::class.java))
            }
            R.id.actions_about_third_party_licenses -> {
                startActivity(Intent(this, LicensesActivity::class.java))
            }
            R.id.actions_about_check_for_updates -> {
                Updates.checkForUpdates(
                    context = this,
                    Updates.getDefaultUpdater(context = this),
                    popupDialog = true,
                    listener = null
                )
            }
            else -> return false
        }
        return true
    }
}