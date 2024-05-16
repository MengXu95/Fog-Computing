package mengxu.taskscheduling.dag;

public class DAGTypePath {
    public DAGTypePath(){
    }

    public static String getDAGTypePath(DAGType dagType){
        String dagTypePath = "";
        switch (dagType) {
            case CyberShake30:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\CyberShake_30.xml";
                break;
            case Epigenomics24:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Epigenomics_24.xml";
                break;
            case Inspiral30:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Inspiral_30.xml";
                break;
            case Montage25:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Montage_25.xml";
                break;
            case Sipht30:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Sipht_30.xml";
                break;
            case CyberShake50:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\CyberShake_50.xml";
                break;
            case Epigenomics46:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Epigenomics_46.xml";
                break;
            case Inspiral50:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Inspiral_50.xml";
                break;
            case Montage50:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Montage_50.xml";
                break;
            case Sipht60:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Sipht_60.xml";
                break;
            case CyberShake100:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\CyberShake_100.xml";
                break;
            case Epigenomics100:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Epigenomics_100.xml";
                break;
            case Inspiral100:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Inspiral_100.xml";
                break;
            case Montage100:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Montage_100.xml";
                break;
            case Sipht100:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Sipht_100.xml";
                break;
            case CyberShake1000:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\CyberShake_1000.xml";
                break;
            case Epigenomics997:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Epigenomics_997.xml";
                break;
            case Inspiral1000:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Inspiral_1000.xml";
                break;
            case Montage1000:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Montage_1000.xml";
                break;
            case Sipht1000:
                dagTypePath = "D:\\xumeng\\ZheJiangLab\\Fog-Computing\\src\\mengxu\\taskscheduling\\dag\\dax\\Sipht_1000.xml";
                break;
            default:
                System.out.println("DAG path error!!!");
                break;
        }
        return dagTypePath;
    }

    public static String getDAGTypePath(int dagTypeID){
        String dagTypePath = "";
        if(dagTypeID == DAGType.CyberShake30.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/CyberShake_30.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics24.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Epigenomics_24.xml";

        }
        else if(dagTypeID == DAGType.Inspiral30.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Inspiral_30.xml";
        }
        else if(dagTypeID == DAGType.Montage25.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Montage_25.xml";

        }
        else if(dagTypeID == DAGType.Sipht30.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Sipht_30.xml";
        }
        else if(dagTypeID == DAGType.CyberShake50.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/CyberShake_50.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics46.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Epigenomics_46.xml";
        }
        else if(dagTypeID == DAGType.Inspiral50.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Inspiral_50.xml";
        }
        else if(dagTypeID == DAGType.Montage50.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Montage_50.xml";
        }
        else if(dagTypeID == DAGType.Sipht60.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Sipht_60.xml";
        }
        else if(dagTypeID == DAGType.CyberShake100.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/CyberShake_100.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics100.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Epigenomics_100.xml";
        }
        else if(dagTypeID == DAGType.Inspiral100.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Inspiral_100.xml";
        }
        else if(dagTypeID == DAGType.Montage100.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Montage_100.xml";
        }
        else if(dagTypeID == DAGType.Sipht100.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Sipht_100.xml";
        }
        else if(dagTypeID == DAGType.CyberShake1000.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/CyberShake_1000.xml";

        }
        else if(dagTypeID == DAGType.Epigenomics997.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Epigenomics_997.xml";
        }
        else if(dagTypeID == DAGType.Inspiral1000.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Inspiral_1000.xml";
        }
        else if(dagTypeID == DAGType.Montage1000.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Montage_1000.xml";
        }
        else if(dagTypeID == DAGType.Sipht1000.value){
            dagTypePath = "/Users/mengxu/IdeaProjects/Fog-Computing/src/mengxu/taskscheduling/dag/dax/Sipht_1000.xml";
        }
        else{
            System.out.println("DAG path error!!!");
        }

        return dagTypePath;
    }

    public static String getDAGTypePathForSubmit(int dagTypeID){
        String dagTypePath = "";
        if(dagTypeID == DAGType.CyberShake30.value){
            dagTypePath = "./dax/CyberShake_30.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics24.value){
            dagTypePath = "./dax/Epigenomics_24.xml";

        }
        else if(dagTypeID == DAGType.Inspiral30.value){
            dagTypePath = "./dax/Inspiral_30.xml";
        }
        else if(dagTypeID == DAGType.Montage25.value){
            dagTypePath = "./dax/Montage_25.xml";
        }
        else if(dagTypeID == DAGType.Sipht30.value){
            dagTypePath = "./dax/Sipht_30.xml";
        }
        else if(dagTypeID == DAGType.CyberShake50.value){
            dagTypePath = "./dax/CyberShake_50.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics46.value){
            dagTypePath = "./dax/Epigenomics_46.xml";
        }
        else if(dagTypeID == DAGType.Inspiral50.value){
            dagTypePath = "./dax/Inspiral_50.xml";
        }
        else if(dagTypeID == DAGType.Montage50.value){
            dagTypePath = "./dax/Montage_50.xml";
        }
        else if(dagTypeID == DAGType.Sipht60.value){
            dagTypePath = "./dax/Sipht_60.xml";
        }
        else if(dagTypeID == DAGType.CyberShake100.value){
            dagTypePath = "./dax/CyberShake_100.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics100.value){
            dagTypePath = "./dax/Epigenomics_100.xml";
        }
        else if(dagTypeID == DAGType.Inspiral100.value){
            dagTypePath = "./dax/Inspiral_100.xml";
        }
        else if(dagTypeID == DAGType.Montage100.value){
            dagTypePath = "./dax/Montage_100.xml";
        }
        else if(dagTypeID == DAGType.Sipht100.value){
            dagTypePath = "./dax/Sipht_100.xml";
        }
        else if(dagTypeID == DAGType.CyberShake1000.value){
            dagTypePath = "./dax/CyberShake_1000.xml";
        }
        else if(dagTypeID == DAGType.Epigenomics997.value){
            dagTypePath = "./dax/Epigenomics_997.xml";
        }
        else if(dagTypeID == DAGType.Inspiral1000.value){
            dagTypePath = "./dax/Inspiral_1000.xml";
        }
        else if(dagTypeID == DAGType.Montage1000.value){
            dagTypePath = "./dax/Montage_1000.xml";
        }
        else if(dagTypeID == DAGType.Sipht1000.value){
            dagTypePath = "./dax/Sipht_1000.xml";
        }
        else{
            System.out.println("DAG path error!!!");
        }

        return dagTypePath;
    }
}
