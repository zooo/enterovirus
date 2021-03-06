# Stakeholder Requirements Specification

## Introduction

### Business purpose

*(Describe the reason and background for pursuing new business or changing the current business.)*

Revision control is needed for serious documentation system which includes multiple related documents and/or collaborate between multiple people. Examples includes:

+ Enterprise rules/regulations and requirement/design documents.
+ Enactment of legal system/laws *(Better word?)* *(It seems only Civil law fits. Common law fits better to a database/search engine?)*
+ Research/knowledge management notes and papers, ...

However, while there are various different tools for source code revision control and collaboration, similar tool does not exist for documents. Alternative solutions includes:

+ Google Drive or similar services
    + Pros:
        + On-line word processor.
        + Multiple users can collaborate and edit one single document *simultaneously*.
        + Support reviewing and comments.
        + Minimal version control.
    + Cons:
        + Content is not separated from presentation.
        + Can't edit documents offline.
        + Blur boundary between editing and reading mode.
        + Major functions only support activities of *one single* document.
        + Can't support larger projects with multiple document files involved.
        + Can only setup links between documents through raw URL. Does not support traceability.
        + Can't integrate with project management.
+ Atlassian Confluence or similar services
    + Pros:
        + On-line word processor.
        + Support reviewing and comments.
        + Support a project with multiple documents. Can easily cross-link between documents with a better interface than raw URL.
        + Integrate with project management (through JIRA). Can link JIRA ticket in documents and update status.
        + Minimal version control.
    + Cons:
        + Content is not separated from presentation.
        + Can't edit documents offline.
        + Can't track cross-documents versioning and changes.
        + Does not support traceability.
        + Versioning get confused when multiple users edit one single document before publishing. No explicit mechanism resolving conflict.
+ Read the Docs
    + Pros:
        + Support a project with multiple documents.
        + Content is separated from presentation.
    + Cons:
        + Does not support reviewing and comments.
        + Can't integrate with project management.
        + Actual version control visualization can only be done through other sites (GitHub, Gitlab, ...).
        + No way to setup links between documents. Does not support traceability.
+ GitHub/Gitlab
    + Pros:
        + Version control of the project as a whole.
        + Support reviewing and comments.
    + Cons:
        + Only have minimal presentation mode.
        + Reviewing and comments can only be done through diff/tiny reviewing cycle (and overfit to code review). It is not the way people read documents.
        + Can only setup links between documents through raw URL. Does not support traceability.
+ Wiki:
    + Pros:
        + Support a project with multiple documents by CamelCase names and "link pattern".
        + Content is separated from presentation.
        + Version control.
    + Cons:
        + Links between documents are in a less official and anarchical way.
        + Version control only based on one single document.
        + Does not support reviewing and comments.
        + Can't integrate with project management.
+ Greenlight Guru:
    + Pros:
        + Specified for medical device document and quality management. Support traceability, risk management, ...
    + *(Not sure if reviewing can be down online or offline?)*
    + Cons:
        + No version control. Use naive and simple version numbers.
        + Workflow is too specific for particular purposes.
        + Not scaleable to large projects.

We are going to make a tool which provide the flexibility and complicity of a source code revision control system, but fits better to document collaboration.

*(TODO: consider rewrite this list to a table of supported/not supported of each feature for each product.)*

On the other hand, even for companies which don't mandatorily need a documentation system, they may be benefit using it by:

+ To get a consistent set of requirements for some prototyping/concept product.
+ To avoid mixing design decisions with requirements/user needs.
+ To build the logic pipeline starting from stakeholder requirements all the way done to test cases.

### Business scope

*(Identifying the business domain by name.)*

+ Enterprise software
+ Business tool

*(Defining the range of business activities included in the business domain concerned.)*

+ Requirement engineering
+ Design control
+ Reviewing
+ Project management

*(Describing the scope of the system being developed or changed.)*

Currently, we only aim on serving enterprise requirement/design documents.

### Business overview

## References

## Definitions, acronyms, and abbreviations

### Definition

+ **Revision control system:** A standalone software application that tracks and provides control over changes to source code.
+ **Revision control platform:** A web-based revision control repository and Internet hosting service.
+ **Word processor:** A computer software application, that performs the task of composing, editing, formatting, and printing of documents.

### Acronyms and abbreviations

## Stakeholders

*(List the stakeholders or the classes of stakeholders and describe how they will influence the organization and business, or will be related to the development and operation of the system.)*

We'll divide our stakeholders into three different parties.

Customer:

- R&D engineers (users).
- Companies which obey quality control procedure.
- Regulatory/auditing departments.

Technical:

- Developers (of this product).
- Operational engineers (of this product).
- Infrastructure provider.

Business:

- Owners. *(profitability/market share/succession planning)*
- Distributor/partner.
- Shareholders/investors. *(return of investment/income)*
- Employees. *(Job satisfaction)*

These parties are equally important for the success of the product. But in this series of documents, we'll concentrate mostly for the customer and technical concerns.

## User requirements

### User needs

Regulatory/field of usage:

- [StRS-0043]{} The software shall be used for official documents.
- [StRS-0042]{} The software shall be used for R&D activities of a future product.

- [StRS-0041]{StRS-0042} The software shall be used while executing the quality control procedure.
- [StRS-0010]{StRS-0041} The software shall be provide supports of common regulatory standards.
- [StRS-0014]{StRS-0010} The software may be used for development of quality sensitive products.
- [StRS-0015]{StRS-0014} The software may be used for medical device industry.
- [StRS-0035]{StRS-0014} The software may be used for automotive industry.
- [StRS-0016]{StRS-0014} The software may be used in energy related industries.
- [StRS-0017]{StRS-0014} The software may be used in transportation related industries.
- [StRS-0013]{StRS-0043} The software may be used for software development.

Features:

- [StRS-0018]{} The software shall assist various activities to establish, maintain, and use of the documentation system.
- [StRS-0001]{StRS-0018} The software shall assist editing document.
- [StRS-0020]{StRS-0001} The software shall give flexible for user to use their familiar word processors.
- [StRS-0062]{StRS-0018} The software shall supporting document reading/viewing.
- [StRS-0036]{StRS-0062} The software shall support basic document formatting.
- [StRS-0037]{StRS-0001,StRS-0062} The software shall separate the content and the presentation of the documents.
- [StRS-0045]{StRS-0018} The software shall support document reviewing and approval.
- [StRS-0058]{StRS-0042,StRS-0045} The software shall support different project management procedures.
- [StRS-0003]{StRS-0058} The software shall support waterfall life cycle model.
- [StRS-0005]{StRS-0058} The software shall support Agile life cycle model.
- [StRS-0061]{StRS-0018} The software shall supporting document achieving.
- [StRS-0046]{StRS-0043,StRS-0018} The software shall support be used to track benchmark/version of documents.

- [StRS-0059]{StRS-0042} The software shall support different kind of documents.
- [StRS-0002]{StRS-0059} The software shall be used for requirement engineering documents.
    - *(Should the software also provide tools for brainstorming of requirements/features? -- The actually process may be similar to comments on the review meeting, but it happens BEFORE a document even exist.)*
- [StRS-0055]{StRS-0002} The software shall be integrate to some feature list/pool, which may be used for e.g. the marketing research or customer support departments.
- [StRS-0008]{StRS-0059} The software shall be used for design control documents.
- [StRS-0044]{StRS-0041,StRS-0059} The software shall be able to manage traceability in/between different layers.
- [StRS-0039]{StRS-0044,StRS-0013} The software shall be able to trace to downstream implementation in case applied (e.g. test cases of codes).

- [StRS-0047]{} The software shall help managing a document set of multiple related documents involved in it.
- [StRS-0049]{StRS-0047} The software shall handle the case only part of the files are documents, while the other part includes code, graphs, historical documents in other format ...

*(Risk analysis?)*

Procedure:

### Operational scenarios

- [StRS-0040]{StRS-0042} The software shall support collaborations.
- [StRS-0009]{StRS-0040} The software shall support multiple teams/users working together for a single product/document.
- [StRS-0048]{StRS-0009} The software shall support multiple people to act as different roles to contribute to the document.
- [StRS-0011]{StRS-0048} The software shall support different access level of the document.

- [StRS-0057]{} The software shall keep the flexibility that user(s) only commit to part of the provided features.

### Operational environment

- [StRS-0006]{} Both local files/applications and web services are needed while using this software.

### Performance

### Operational life cycle

### User and operator characteristics

## Concepts of proposed system

### Operational concept

### Operational scenario

## Business management requirements

### Business environment

*(Market trends)*

- [StRS-0026]{} The software shall aim to provide services that does not exist, for unfulfilled user needs.
- [StRS-0019]{StRS-0020,StRS-0026} The software shall not provide duplicated functions of an (on-line or local) word processor, such as Microsoft Office, Google Doc, Emacs, Vim, or Atom.
- [StRS-0025]{StRS-0026} With the aim of handling multiple files in a documentation system, the software shall not provide tools for multiple users to edit the same file simultaneously, such as Google Drive.
- [StRS-0012]{StRS-0026} The software shall not provide duplicated functions of a code revision control platform, such as GitHub or BitBucket.

*(Laws and regulations)*

- [StRS-0027]{} The development of the software itself shall strictly follow quality procedure, hence user can refer to the use of it to regulatory departments.
- [StRS-0029]{StRS-0027} The software itself shall have complete requirement/design documents.
- [StRS-0028]{} The development team of this software may consider getting CMMI/SWEBOK certification(s), hence to prove to the user the reliability/quality and increase the compatibility of this software.

*(Social responsibilities)*

*(Technology)*

### Goal and objective

- [StRS-0024]{} The software shall become the leading (first?) revision control based service for serious documentation system.

### Business model

Target customer:

- [StRS-0054]{} The source code itself for this software, shall be open sourced.
- [StRS-0033]{} The software may be provided as SaaS, built on top of the cloud.
- [StRS-0056]{StRS-0033} For SaaS, there shall be (1) public repositories that everybody can read the inside documents, and (2) private repositories that only authorized users have access to it.
- [StRS-0051]{StRS-0056} For SaaS, the software shall be free for public repositories, while it is priced for private repositories.
- [StRS-0034]{} The software may be provided as a standalone product, for the infrastructure hosted and setup in the user's place.
- [StRS-0052]{StRS-0054,StRS-0034} The software shall be free for individual users who build from source code and hosted locally.
- [StRS-0053]{StRS-0034} The software shall be priced for business users, for a cost of both (1) a required one time license fee, and (2) optional maintenance services.

Team building:

- [StRS-0021]{} The software shall become an independent service for quality control documentation system in the short term.
- [StRS-0030]{} The development of this software shall be kept in a small R&D team in the beginning to keep largest flexibility.
- [StRS-0031]{} The software shall first includes primary functions, and provide them to the beta user as soon as possible.
- [StRS-0032]{} The software R&D team may extend when (1) secondary functions, and (2) serious tests are included in the development activities.
- [StRS-0022]{} The software may later modify its functionalities to other fields, hence extend itself to other markets such as (1) enterprise rules/regulations documents, (2) enactment of legal system/laws, (3) research/knowledge management notes and papers.
- [StRS-0023]{} For the software R&D related documents, the software may later be integrated into a code revision control platform.

### Information environment

#### Project portfolio

*(When multiple system projects are running or planned to pursue the same business goal, the priority, relative positioning, and possible constraints come from the portfolio management strategy.)*

#### Long term system plan

*(When common system infrastructure or architecture has been decided or planned, it should be described as constraints on possible design decisions.)*

#### Database configuration

*(An organization level database configuration plan and possible constraints on availability and accessibility of organization global data.)*

## Business operational requirements

### Business processes

### Business operational policies and rules

### Business operational constrains

### Business operational modes

### Business operational quality

### Business structure

## Project Constrains

## Bibliography

1. ISO/IEC/IEEE 29148:2011, System and software engineering -- Life cycle processes -- Requirement engineering.
2. ISO/IEC/IEEE 15288:2008, System and software engineering -- System life cycle process.
3. A guide to the Project Management Body of Knowledge (PMBOK Guide).
