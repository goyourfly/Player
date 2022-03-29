package com.obsez.android.lib.smbfilechooser.internals;

import java.util.regex.Pattern;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

/**
 * Created by coco on 6/7/15. Edited by Guiorgy on 10/09/18.
 */
public class RegexSmbFileFilter implements SmbFileFilter {
    private boolean m_allowHidden;
    private boolean m_onlyDirectory;
    private Pattern m_pattern;

    public RegexSmbFileFilter() {
        this(null);
    }

    public RegexSmbFileFilter(Pattern ptn) {
        this(false, false, ptn);
    }

    public RegexSmbFileFilter(boolean dirOnly, boolean hidden, String ptn) {
        m_allowHidden = hidden;
        m_onlyDirectory = dirOnly;
        m_pattern = Pattern.compile(ptn, Pattern.CASE_INSENSITIVE);
    }

    public RegexSmbFileFilter(boolean dirOnly, boolean hidden, String ptn, int flags) {
        m_allowHidden = hidden;
        m_onlyDirectory = dirOnly;
        m_pattern = Pattern.compile(ptn, flags);
    }

    public RegexSmbFileFilter(boolean dirOnly, boolean hidden, Pattern ptn) {
        m_allowHidden = hidden;
        m_onlyDirectory = dirOnly;
        m_pattern = ptn;
    }

    @Override
    public boolean accept(SmbFile pathname) throws SmbException {
        if (!m_allowHidden) {
            if (pathname.isHidden()) {
                return false;
            }
        }

        if (m_onlyDirectory) {
            if (!pathname.isDirectory()) {
                return false;
            }
        }

        if (m_pattern == null) {
            return true;
        }

        if (pathname.isDirectory()) {
            return true;
        }

        String name = pathname.getName();
        return m_pattern.matcher(name).matches();
    }
}
