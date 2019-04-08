# Replace the $VARIABLES with real stuff

# Creating a new branch:
svn copy https://github.com/BlockAppDev/FocusKeeper.git/trunk https://github.com/BlockAppDev/FocusKeeper.git/branches/$NEW_BRANCH_NAME -m "$COMMIT_MESSAGE"

# Checking a branch out for use:
svn checkout https://github.com/BlockAppDev/FocusKeeper.git/branches/$NEW_BRANCH_NAME