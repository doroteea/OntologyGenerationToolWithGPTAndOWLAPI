export class ObjectPropertyAxiomsMetrics {
  subObjectPropertyAxioms: number| undefined;
  equivalentObjectPropertyAxioms: number| undefined;
  inverseObjectPropertyAxiomsMetric: number| undefined;
  disjointObjectPropertyAxiomsMetric: number| undefined;
  functionalObjectPropertyAxiomsMetric: number| undefined;
  inverseFunctionalObjectPropertiesAxiomsMetric: number| undefined;
  transitiveObjectPropertyAxiomsMetric: number| undefined;
  symmetricObjectPropertyAxiomsMetric: string| undefined;
  asymmetricObjectPropertyAxiomsMetric: Set<string>| undefined;
  reflexiveObjectPropertyAxiomsMetric: Set<string>| undefined;
  irreflexiveObjectPropertyAxiomsMetric: Set<string>| undefined;
  objectPropertyDomainAxiomsMetric: number| undefined;
  objectPropertyRangeAxiomsMetric: number| undefined;
  subPropertyChainOfAxiomsMetric: number| undefined;
}
