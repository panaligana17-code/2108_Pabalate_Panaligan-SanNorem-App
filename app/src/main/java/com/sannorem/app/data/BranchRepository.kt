package com.sannorem.app.data

import com.sannorem.app.domain.model.RetailBranch

interface BranchRepository {
    fun getBranches(): List<RetailBranch>
}
