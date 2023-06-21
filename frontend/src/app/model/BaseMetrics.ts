export class BaseMetrics {
  axioms: number| undefined;
  logicalAxiomsCount: number| undefined;
  classCount: number| undefined;
  anonymousClassCount: number| undefined;
  objectPropertyCount: number| undefined;
  dataPropertyCount: number| undefined;
  individualsCount: number| undefined;
  dlExpresivity: string| undefined;
  directImports: Set<string>| undefined;
  declaredImports: Set<string>| undefined;
  actualImports: Set<string>| undefined;
}
