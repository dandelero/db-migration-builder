#!/bin/sh

# Converts a relative path to an absolute path, with thanks to
# https://stackoverflow.com/questions/3572030/bash-script-absolute-path-with-os-x
realpath() {
    [[ $1 = /* ]] && echo "$1" || echo "$PWD/${1#./}"
}

# Displays usage information and exits.
usage() {
  echo "Usage: $0
    [-d <database>] the type of database to generate migrations for
    [-i <input directory>] the path to the input directory containing the modules/versions
    [-o <output directory>] the path to the output directory to write to
    [-v <version>] [optional] the version to be processed
    [-k <version scheme>] [optional] the scheme by which the versions abide
    [-m <module1, module2, ..., moduleN>] [optional] the names of the modules to be processed as a CSV string
  " 1>&2;
  exit 1;
}

# Parses the script args to set up the execution environment.
parseArgs() {
  while getopts :d:i:k:m:o:v:h option; do
      case "$option" in
          d)
              DATABASE_ENGINE="${OPTARG}"
              ;;
          i)
              INPUT_DIRECTORY=$OPTARG
              ;;
          k)
              VERSION_SCHEME=$OPTARG
              ;;
          m)
              REQUESTED_MODULES=${OPTARG}
              ;;
          o)
              OUTPUT_DIRECTORY=${OPTARG}
              ;;
          v)
              REQUESTED_VERSION=${OPTARG}
              ;;
          *)
              usage
              ;;
      esac
  done
}

# Validates the input args.
validateArgs() {
  if [ -z "${DATABASE_ENGINE}" ] || [ -z "${INPUT_DIRECTORY}" ] || [ -z "${OUTPUT_DIRECTORY}" ]; then
      usage
  fi

  if [ ! -z $SCRIPT_SETTINGS_FILE ] && [ ! -f $SCRIPT_SETTINGS_FILE ]; then
    echo "Script settings not found: $SCRIPT_SETTINGS_FILE"
    usage
  fi

  if [ ! -d $INPUT_DIRECTORY ]; then
      echo "Input directory not found: $INPUT_DIRECTORY"
      usage
  fi

  if [ -z "${VERSION_SCHEME}" ]; then
      echo "No version scheme provided"
      usage
  fi
}

# Calls on the jar file with the required arguments.
invokeApplication() {
  # Find the main jar.
  # Add in the 3 core arguments.
  APPLICATION_ARGS="--input-directory-path $INPUT_DIRECTORY --output-directory-path $OUTPUT_DIRECTORY --database-engine $DATABASE_ENGINE"

  # Version: if not provided => run for the latest version.
  if [ ! -z "${REQUESTED_VERSION}" ]; then
    APPLICATION_ARGS="$APPLICATION_ARGS --version $REQUESTED_VERSION"
  fi

  # Module(s): if not provided => run for the default module.
  if [ ! -z "${REQUESTED_MODULES}" ]; then
    APPLICATION_ARGS="$APPLICATION_ARGS --modules $REQUESTED_MODULES"
  fi

  # Settings file.
  if [ ! -f $OVERRIDE_CONFIG_FILE ]; then
    APPLICATION_ARGS="$APPLICATION_ARGS --config-file-override $OVERRIDE_CONFIG_FILE"
  fi

  # Version scheme.
  APPLICATION_ARGS="$APPLICATION_ARGS --version-scheme $VERSION_SCHEME"

  # Run.
  command="$JAVA_EXE -jar $APPLICATION_JAR_FILE $APPLICATION_ARGS"
  eval $command
}

# Sets up the environment for running the application.
setupEnvironment() {
  # Find the main jar file.
  for f in `find $APP_LIB_DIRECTORY -name $APP_JAR_NAME_PATTERN`; do
    APPLICATION_JAR_FILE=$f
  done

  if [ -z $APPLICATION_JAR_FILE ]; then
    echo "Application jar file ($APP_JAR_NAME_PATTERN) not found in $APP_LIB_DIRECTORY"
    exit 1
  fi

  # Now find the java exe.
  JAVA_HOME=`env | grep JAVA_HOME`
  if [ -z $JAVA_HOME ]; then
    JAVA_EXE=`which java`
  else
    JAVA_EXE=$JAVA_HOME/bin/java
  fi

  if [ ! -f $JAVA_EXE ]; then
    echo "No java executable found"
    exit 1
  fi
}


# The name pattern of the jar containing our main class.
APP_JAR_NAME_PATTERN="db-migration-cli*.jar"

# The directory the script resides in.
APP_SCRIPT_DIRECTORY=`realpath $0 | xargs dirname`

# The base directory where the application resides.
APP_BASE_DIRECTORY=`dirname $APP_SCRIPT_DIRECTORY | xargs dirname`

# The conf directory.
APP_CONF_DIRECTORY="$APP_BASE_DIRECTORY/conf"

# The lib directory.
APP_LIB_DIRECTORY="$APP_BASE_DIRECTORY/libs"

# The path to the override config file.
OVERRIDE_CONFIG_FILE="$APP_CONF_DIRECTORY/config.yaml"

# The full path to the main jar file.
APPLICATION_JAR_FILE=""

# The java executable.
JAVA_EXE="java"

# The modules to be processed.
REQUESTED_MODULES=""

# The version of the module(s) to be processed.
REQUESTED_VERSION=""

# The scheme by which the versions abide.
VERSION_SCHEME="default"

# The input directory where the modules/version reside.
INPUT_DIRECTORY=""

# The output directory to write the output to.
OUTPUT_DIRECTORY=""

# The database engine to generate the migration scripts for.
DATABASE_ENGINE=""

# Main!
parseArgs $*
validateArgs
setupEnvironment
invokeApplication
