package de.fuberlin.wiwiss.silk.workbench.lift.comet

class GenerateLinksHelp extends LinksHelp {

  override def renderOverview = {
    <span>
      Executes the current linkage rule.
      { howToRateLinks }
    </span>
  }
}