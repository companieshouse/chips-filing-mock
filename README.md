# proof-of-concept

## Description

This repository is used for all proof of concepts. Please keep any code out of `master` and instead push your code on a suitably named branch

## Branch Naming Conventions

- Do **not** include `poc` or `proof-of-concept` in your branch name. The repository gives this context implicitly
- If importing branches from another repository name them as : `IMPORT_REPOSITORY/POC_BRANCH`
    - **Do this**:
        - e.g. `document-loader/import-branch-1`
        - e.g. `document-loader/import-branch-2`
    - **Not this** (this will result in name collisions):
        - `document-loader`
        - `document-loader/feature-1`

## Importing from another repository

To import an existing repository while maintaining the history do the following:
- Checkout the repository you'll be importing
    - `git clone git@github.com:companieshouse/YOUR_REPOSITORY.git`
- Create the branch you want to have in the `proof-of-concept` repository
    - `git checkout -b import-branch` *(See naming conventions)*
- Add the `proof-of-concept` remote repository
    - `git remote add REMOTE_NAME git@github.com:companieshouse/proof-of-concept.git`
    - e.g. `git remote add poc git@github.com:companieshouse/proof-of-concept.git`
- Push your branch
    - `git push REMOTE_NAME import-branch`
    - e.g. `git push poc import-branch`
