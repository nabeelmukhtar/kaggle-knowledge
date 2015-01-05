#!/usr/bin/Rscript
# File-Name:       package_installer.R           
# Date:            2013-09-06                         
# Author:          Nabeel Mukhtar
# Purpose:         Install all of the packages needed for the Kaggle Machine Learning scripts
# Data Used:       n/a
# Packages Used:   n/a

# Create a vector containing all of the packages that will be used in ML scripts
# (in no particular order)

options(repos=structure(c(CRAN="http://cran.stat.auckland.ac.nz/")))

args <- commandArgs(trailingOnly = TRUE)

core.packages <- c(
  # core packages
  "devtools", 
  "getopt",
  "rjson",
  "RJSONIO",
  "stringr",
  # interface packages
  rJava = "http://cran.r-project.org/src/contrib/rJava_0.9-6.tar.gz",
  "rmongodb",
  "RSQLite",
  "RCurl",
  # caching/performance/parallelism packages
  "R.cache",
  "ff",
  "ffbase",
  "foreach",
  "snow",
  "parallel",
  "doMC",
  # data munging packages
  "data.table",
  "reshape",
  "reshape2",
  "plyr",
  "dplyr",
  "COUNT",
  "dummies",
  # statistics/machine learning packages
  "NMF",
  "caret",
  "stats",
  "e1071",
  "Hmisc",
  "cluster",
  "fpc", 
  "orclus",
  "DMwR",
  "flexclust",
  "weightedKmeans",
  Rlof = "http://cran.r-project.org/src/contrib/Archive/Rlof/Rlof_1.0.0.tar.gz",
  # "pmclust", # trouble installing
  "rpart",
  "party",
  "arules",
  "igraph",
  "tm",
  "RTextTools",
  # market research packages
  "prefmod",
  "psych",
  "conjoint",
  "softImpute",
  "ChoiceModelR",
  "mlogit",
  "lavaan",
  "RCA",
  "mirt",
  "bayesm",
  "basicspace",
  "difR"
)

dev.packages <- c("RUnit", "codetools", "tools", "sig", "assertive", "testthat", "roxygen2")

report.packages <- c("ggplot2", "xlsx", "XLConnect", "gridExtra", "rCharts", "xtable", "pander", "knitr")

rhadoop.packages <- c(rmr2 = "https://raw.github.com/RevolutionAnalytics/rmr2/master/build/rmr2_2.2.2.tar.gz",
                      rhdfs = "https://raw.github.com/RevolutionAnalytics/rhdfs/master/build/rhdfs_1.0.6.tar.gz",
                      rhbase = "https://raw.github.com/RevolutionAnalytics/rhbase/master/build/rhbase_1.2.0.tar.gz")           

install_package <- function(x, dependencies = TRUE, type = "source") {
  if (any(grepl('^http[s]?\\://', x))) {
    install_url(x)
  } else if (any(grepl('^git\\:', x))) {
    # FIXME: extract user/repo name
    install_github(x)
  } else if (any(grepl('^bitbucket\\:', x))) {
    # FIXME: extract user/repo name
    install_bitbucket(x)
  } else if (any(grepl('^file\\:', x))) {
    # FIXME: extract file path
    install(x)
  } else {
    install.packages(x, dependencies = dependencies, type = type)
  }
}

cat("This script will now attempt to install all of the R packages used in Elastica Machine Learning scripts.\n")

names(core.packages)[names(core.packages) == ""] <- core.packages[names(core.packages) == ""]
names(dev.packages)[names(dev.packages) == ""] <- dev.packages[names(dev.packages) == ""]
names(report.packages)[names(report.packages) == ""] <- report.packages[names(report.packages) == ""]

for(p in names(core.packages)) {
  if(!suppressWarnings(require(p, character.only = TRUE, quietly = TRUE))) {
    cat(paste(p, "missing, will attempt to install...\n"))
    install_package(core.packages[p])
    require(p, character.only = TRUE, quietly = FALSE)
  } else {
    cat(paste(p, "installed OK...\n"))
  }
}

print("### All required packages installed ###")

if ("--dev" %in% args) {
  cat("This script will now attempt to install all of the R packages used for development.\n")
  
  for(p in names(dev.packages)) {
    if(!suppressWarnings(require(p, character.only = TRUE, quietly = TRUE))) {
      cat(paste(p, "missing, will attempt to install...\n"))
      install_package(dev.packages[p])
      require(p, character.only = TRUE, quietly = FALSE)
    } else {
      cat(paste(p, "installed OK...\n"))
    }
  }
  
  print("### All dev packages installed ###")
}

if ("--report" %in% args) {
  cat("This script will now attempt to install all of the R packages used for reporting.\n")
  
  for(p in names(report.packages)) {
    if(!suppressWarnings(require(p, character.only = TRUE, quietly = TRUE))) {
      cat(paste(p, "missing, will attempt to install...\n"))
      install_package(report.packages[p])
      require(p, character.only = TRUE, quietly = FALSE)
    } else {
      cat(paste(p, "installed OK...\n"))
    }
  }
  
  print("### All report packages installed ###")
}

if ("--hadoop" %in% args) {
  cat("This script will now attempt to install R packages for Hadoop.\n")
  for(p in names(rhadoop.packages)) {
    if(!suppressWarnings(require(p, character.only = TRUE, quietly = TRUE))) {
      cat(paste(p, "missing, will attempt to install...\n"))
      install_package(rhadoop.packages[p])
      require(p, character.only = TRUE, quietly = FALSE)
    } else {
      cat(paste(p, "installed OK...\n"))
    }
  }
  
  print("### All RHadoop packages installed ###")
}
